package org.siwoong.muse.admin;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.siwoong.muse.admin.dto.ProfanityItemView;
import org.siwoong.muse.admin.dto.ProfanityItemView.ContentType;

import org.siwoong.muse.column.ColumnComment;
import org.siwoong.muse.column.ColumnCommentRepository;
import org.siwoong.muse.song.SongReview;
import org.siwoong.muse.song.SongReviewComment;
import org.siwoong.muse.song.repository.SongReviewCommentRepository;
import org.siwoong.muse.song.repository.SongReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final SongReviewRepository songReviewRepository;
    private final SongReviewCommentRepository songReviewCommentRepository;
    private final ColumnCommentRepository columnCommentRepository;

    @Transactional(readOnly = true)
    public List<ProfanityItemView> getAllFlaggedContent() {
        List<ProfanityItemView> result = new ArrayList<>();

        // 1) 곡 리뷰
        for (SongReview r : songReviewRepository.findByHasProfanityTrueOrderByCreatedAtDesc()) {
            ProfanityItemView v = new ProfanityItemView();
            v.setType(ContentType.SONG_REVIEW);
            v.setId(r.getId());
            v.setSnippet(cutSnippet(r.getContent()));
            v.setAuthorNickname(r.getUser().getNickname());
            v.setCreatedAt(r.getCreatedAt());
            // 곡 상세 페이지 링크
            Long songId = r.getSong().getId();
            v.setTargetLink("/songs/" + songId);

            result.add(v);
        }

        // 2) 곡 리뷰 댓글
        for (SongReviewComment c : songReviewCommentRepository.findByHasProfanityTrueOrderByCreatedAtDesc()) {
            ProfanityItemView v = new ProfanityItemView();
            v.setType(ContentType.SONG_REVIEW_COMMENT);
            v.setId(c.getId());
            v.setSnippet(cutSnippet(c.getContent()));
            v.setAuthorNickname(c.getWriter().getNickname());
            v.setCreatedAt(c.getCreatedAt());
            Long songId = c.getReview().getSong().getId();
            v.setTargetLink("/songs/" + songId);

            result.add(v);
        }

        // 3) 칼럼 댓글
        for (ColumnComment c : columnCommentRepository.findByHasProfanityTrueOrderByCreatedAtDesc()) {
            ProfanityItemView v = new ProfanityItemView();
            v.setType(ContentType.COLUMN_COMMENT);
            v.setId(c.getId());
            v.setSnippet(cutSnippet(c.getContent()));
            v.setAuthorNickname(c.getUser().getNickname());
            v.setCreatedAt(c.getCreatedAt());
            Long columnId = c.getColumnPost().getId();
            v.setTargetLink("/columns/" + columnId);

            result.add(v);
        }

        // 필요하면 createdAt 으로 한 번 더 정렬해도 됨
        result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return result;
    }

    private String cutSnippet(String content) {
        if (content == null) return "";
        content = content.trim();
        if (content.length() <= 80) {
            return content;
        }
        return content.substring(0, 80) + "...";
    }
}
