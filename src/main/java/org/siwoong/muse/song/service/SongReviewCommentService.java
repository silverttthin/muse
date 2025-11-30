package org.siwoong.muse.song.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.profanity.ProfanityClient;
import org.siwoong.muse.song.SongReview;
import org.siwoong.muse.song.SongReviewComment;
import org.siwoong.muse.song.repository.SongReviewCommentRepository;
import org.siwoong.muse.song.repository.SongReviewRepository;
import org.siwoong.muse.user.User;
import org.siwoong.muse.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongReviewCommentService {

    private final SongReviewCommentRepository commentRepository;
    private final SongReviewRepository songReviewRepository;
    private final UserRepository userRepository;
    private final ProfanityClient profanityClient;

    /**
     * 리뷰 리스트에 대해 댓글들을 한 번에 조회해서
     * reviewId -> List<Comment> 형태로 묶어서 반환
     */
    public Map<Long, List<SongReviewComment>> getCommentsGroupedByReview(List<SongReview> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return Collections.emptyMap();
        }

        List<SongReviewComment> comments =
            commentRepository.findByReviewInAndDeletedFalseOrderByCreatedAtAsc(reviews);

        Map<Long, List<SongReviewComment>> map = new HashMap<>();
        for (SongReviewComment c : comments) {
            Long reviewId = c.getReview().getId();
            map.computeIfAbsent(reviewId, k -> new ArrayList<>()).add(c);
        }
        return map;
    }

    @Transactional
    public void addComment(Long userId, Long reviewId, String content) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 내용을 입력하세요.");
        }

        User writer = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        SongReview review = songReviewRepository.findByIdAndDeletedFalse(reviewId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        // 욕설 필터링
        Boolean hasProfanity = profanityClient.isProfanity(content);


        SongReviewComment comment = SongReviewComment.builder()
            .writer(writer)
            .review(review)
            .content(content.trim())
            .deleted(false)
            .hasProfanity(hasProfanity)
            .build();

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        SongReviewComment comment = commentRepository.findByIdAndDeletedFalse(commentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."));

        if (!comment.getWriter().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 댓글만 삭제할 수 있습니다.");
        }

        comment.softDelete();
    }
}
