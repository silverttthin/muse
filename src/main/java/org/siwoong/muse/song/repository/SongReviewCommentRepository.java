package org.siwoong.muse.song.repository;

import java.util.List;
import java.util.Optional;
import org.siwoong.muse.song.SongReview;
import org.siwoong.muse.song.SongReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongReviewCommentRepository extends JpaRepository<SongReviewComment, Long> {
    // 여러 리뷰에 달린 댓글들을 한 번에 가져오기
    List<SongReviewComment> findByReviewInAndDeletedFalseOrderByCreatedAtAsc(
        List<SongReview> reviews);

    Optional<SongReviewComment> findByIdAndDeletedFalse(Long id);

    List<SongReviewComment> findByHasProfanityTrueOrderByCreatedAtDesc();

}
