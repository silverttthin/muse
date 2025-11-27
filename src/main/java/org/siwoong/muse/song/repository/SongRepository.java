package org.siwoong.muse.song.repository;

import java.util.List;
import org.siwoong.muse.song.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongRepository extends JpaRepository<Song, Long> {
    // 필요하면 이런 거도 사용 가능
    List<Song> findTop50ByOrderByIdAsc();

    boolean existsBySpotifyId(String spotifyId);

    // 🔍 제목 또는 아티스트에 검색어 포함 (대소문자 무시)
    List<Song> findTop20ByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(
        String titleKeyword,
        String artistKeyword
    );

    @Query("SELECT s FROM Song s WHERE s.id IN :ids")
    List<Song> findSongsByIdIn(@Param("ids") List<Long> songIds);
}
