package org.siwoong.muse.song.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.profanity.ProfanityClient;
import org.siwoong.muse.song.Song;
import org.siwoong.muse.song.SongReview;
import org.siwoong.muse.song.repository.SongRepository;
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
public class SongReviewService {

    private final SongReviewRepository songReviewRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ProfanityClient profanityClient;

    public List<SongReview> getReviewsForSong(Long songId) {
        return songReviewRepository.findBySongIdAndDeletedFalseOrderByCreatedAtDesc(songId);
    }

    @Transactional
    public void addReview(Long userId, Long songId, String content) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "리뷰 내용을 입력하세요.");
        }

        // ★ 이미 이 곡에 쓴 리뷰가 있는지 체크 (삭제 안 된 것 기준)
        if (songReviewRepository.existsByUser_IdAndSong_IdAndDeletedFalse(userId, songId)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "이 곡에 대해서는 이미 리뷰를 작성했습니다. 리뷰 수정 기능을 사용하세요."
            );
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "곡을 찾을 수 없습니다."));

        // 욕설 필터링
        Boolean hasProfanity = profanityClient.isProfanity(content);

        SongReview review = SongReview.builder()
            .user(user)
            .song(song)
            .content(content.trim())
            .deleted(false)
            .hasProfanity(hasProfanity)
            .build();

        songReviewRepository.save(review);
    }

    @Transactional
    public void editReview(Long userId, Long reviewId, String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "리뷰 내용을 입력하세요.");
        }

        SongReview review = songReviewRepository.findByIdAndDeletedFalse(reviewId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 욕설 필터링
        Boolean hasProfanity = profanityClient.isProfanity(newContent);
        review.setHasProfanity(hasProfanity);

        review.updateContent(newContent.trim());
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        SongReview review = songReviewRepository.findByIdAndDeletedFalse(reviewId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        review.softDelete();
    }
}
