package org.siwoong.muse.song.repository;

import org.siwoong.muse.song.SongRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SongRatingRepository extends JpaRepository<SongRating, Long> {

    java.util.Optional<SongRating> findByUserIdAndSongId(Long userId, Long songId);

    @Query("""
           select avg(r.score)
           from SongRating r
           where r.song.id = :songId
           """)
    Double findAverageScoreBySongId(Long songId);

    long countBySongId(Long songId);
}
