package org.siwoong.muse.song.repository;

import java.util.List;

import org.siwoong.muse.song.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongRepository extends JpaRepository<Song, Long> {

    // 기본 페이지네이션
    Page<Song> findAll(Pageable pageable);

    // 장르 필터 + 페이지네이션
    // 우측 공백 + 대소문자 차이 무시
    @Query("""
        SELECT s
        FROM Song s
        WHERE LOWER(TRIM(s.playlistGenre)) = LOWER(TRIM(:genre))
        """)
    Page<Song> findByPlaylistGenre(@Param("genre") String genre, Pageable pageable);

    // ===== 검색 (제목/아티스트) =====
    // JPQL에 LIMIT 20 넣은 건 문법적으로 잘못이었고,
    // 애초에 메서드 이름이 findTop20By... 이라서 @Query가 필요 없다.
    // Spring Data가 메서드 이름 보고 TOP 20 + LIKE 검색을 자동 생성한다.
    List<Song> findTop20ByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(
        String titleKeyword,
        String artistKeyword
    );

    @Query("""
        SELECT s
        FROM Song s
        WHERE s.id IN :ids
        """)
    List<Song> findSongsByIdIn(@Param("ids") List<Long> songIds);

    // ====== 평점 높은 순 (GROUP BY + AVG) ======

    @Query(value = """
        SELECT s
        FROM Song s
        LEFT JOIN SongRating r ON r.song = s
        GROUP BY s
        ORDER BY COALESCE(AVG(r.score), 0) DESC
        """,
        countQuery = """
        SELECT COUNT(s)
        FROM Song s
        """)
    Page<Song> findAllOrderByAverageRatingDesc(Pageable pageable);

    @Query(value = """
        SELECT s
        FROM Song s
        LEFT JOIN SongRating r ON r.song = s
        WHERE LOWER(TRIM(s.playlistGenre)) = LOWER(TRIM(:genre))
        GROUP BY s
        ORDER BY COALESCE(AVG(r.score), 0) DESC
        """,
        countQuery = """
        SELECT COUNT(s)
        FROM Song s
        WHERE LOWER(TRIM(s.playlistGenre)) = LOWER(TRIM(:genre))
        """)
    Page<Song> findByPlaylistGenreOrderByAverageRatingDesc(@Param("genre") String genre,
        Pageable pageable);

    // ====== 리뷰 많은 순 (GROUP BY + COUNT) ======

    @Query(value = """
        SELECT s
        FROM Song s
        LEFT JOIN SongReview rv ON rv.song = s AND rv.deleted = false
        GROUP BY s
        ORDER BY COUNT(rv) DESC
        """,
        countQuery = """
        SELECT COUNT(s)
        FROM Song s
        """)
    Page<Song> findAllOrderByReviewCountDesc(Pageable pageable);

    @Query(value = """
        SELECT s
        FROM Song s
        LEFT JOIN SongReview rv ON rv.song = s AND rv.deleted = false
        WHERE LOWER(TRIM(s.playlistGenre)) = LOWER(TRIM(:genre))
        GROUP BY s
        ORDER BY COUNT(rv) DESC
        """,
        countQuery = """
        SELECT COUNT(s)
        FROM Song s
        WHERE LOWER(TRIM(s.playlistGenre)) = LOWER(TRIM(:genre))
        """)
    Page<Song> findByPlaylistGenreOrderByReviewCountDesc(@Param("genre") String genre,
        Pageable pageable);
}
