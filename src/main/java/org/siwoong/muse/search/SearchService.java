package org.siwoong.muse.search;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.song.Song;
import org.siwoong.muse.song.repository.SongRepository;
import org.siwoong.muse.user.User;
import org.siwoong.muse.user.repository.UserRepository;
import org.siwoong.muse.column.ColumnPost;
import org.siwoong.muse.column.ColumnPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final ColumnPostRepository columnPostRepository;

    public SearchResult search(String query) {
        if (query == null || query.isBlank()) {
            return new SearchResult(List.of(), List.of(), List.of());
        }

        String keyword = query.trim();

        // 곡: 제목/아티스트
        List<Song> songs = songRepository
            .findTop20ByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(keyword, keyword);

        // 유저: 닉네임
        List<User> users = userRepository
            .findTop20ByNicknameContainingIgnoreCase(keyword);

        // 칼럼: 제목/내용 + deleted = false
        List<ColumnPost> columns = columnPostRepository
            .searchByKeyword(keyword);

        return new SearchResult(songs, users, columns);
    }

    // 단순 DTO
    public record SearchResult(
        List<Song> songs,
        List<User> users,
        List<ColumnPost> columns
    ) {}
}
