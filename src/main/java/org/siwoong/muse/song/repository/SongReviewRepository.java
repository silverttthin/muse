package org.siwoong.muse.song.repository;

import org.siwoong.muse.song.SongReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongReviewRepository extends JpaRepository<SongReview, Long> {

    // 특정 곡의 삭제되지 않은 리뷰 최신순
    List<SongReview> findBySongIdAndDeletedFalseOrderByCreatedAtDesc(Long songId);

    Optional<SongReview> findByIdAndDeletedFalse(Long id);

    boolean existsByUser_IdAndSong_IdAndDeletedFalse(Long userId, Long songId);

    List<SongReview> findTop20ByUser_IdAndDeletedFalseOrderByCreatedAtDesc(Long userId);


}
