package org.siwoong.muse.column;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.user.User;
import org.siwoong.muse.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColumnCommentService {

    private final ColumnCommentRepository columnCommentRepository;
    private final ColumnPostRepository columnPostRepository;
    private final UserRepository userRepository;

    public List<ColumnComment> getCommentsForColumn(Long columnId) {
        ColumnPost post = columnPostRepository.findByIdAndDeletedFalse(columnId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "칼럼을 찾을 수 없습니다."
            ));

        return columnCommentRepository.findByColumnPostAndDeletedFalseOrderByIdAsc(post);
    }

    @Transactional
    public void addComment(Long userId, Long columnId, String content) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "댓글 내용을 입력해주세요.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
            ));

        ColumnPost post = columnPostRepository.findByIdAndDeletedFalse(columnId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "칼럼을 찾을 수 없습니다."
            ));

        ColumnComment comment = ColumnComment.builder()
            .user(user)
            .columnPost(post)
            .content(content.trim())
            .deleted(false)
            .build();

        columnCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        ColumnComment comment = columnCommentRepository.findByIdAndDeletedFalse(commentId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."
            ));

        // 본인 또는 ADMIN만 삭제 허용
        if (!comment.getUser().getId().equals(userId)
            && comment.getUser().getRole() != org.siwoong.muse.user.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }

        comment.setDeleted(true);
    }
}
