package org.siwoong.muse.column;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.user.User;
import org.siwoong.muse.user.repository.UserRepository;
import org.siwoong.muse.user.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColumnService {

    private final ColumnPostRepository columnPostRepository;
    private final UserRepository userRepository;

    public List<ColumnPost> getLatestColumns() {
        return columnPostRepository.findTop50ByDeletedFalseOrderByCreatedAtDesc();
    }

    public ColumnPost getColumn(Long id) {
        return columnPostRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "칼럼을 찾을 수 없습니다."
            ));
    }

    @Transactional
    public ColumnPost createColumn(Long authorId, String title, String content) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목을 입력해주세요.");
        }
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용을 입력해주세요.");
        }

        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
            ));

        // 권한 체크: CURATOR 또는 ADMIN만 작성 가능
        if (!(author.getRole() == Role.CURATOR || author.getRole() == Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "칼럼 작성 권한이 없습니다.");
        }

        ColumnPost post = ColumnPost.builder()
            .author(author)
            .title(title.trim())
            .content(content.trim())
            .deleted(false)
            .build();

        return columnPostRepository.save(post);
    }

    @Transactional
    public void deleteColumn(Long requesterId, Long columnId) {
        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
            ));

        if (requester.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "칼럼 삭제 권한이 없습니다.");
        }

        ColumnPost post = columnPostRepository.findByIdAndDeletedFalse(columnId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "칼럼을 찾을 수 없습니다."
            ));

        post.softDelete();
    }
}
