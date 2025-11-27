package org.siwoong.muse.column;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnCommentRepository extends JpaRepository<ColumnComment, Long> {

    // 특정 칼럼의 댓글들 (삭제 안 된 것만), 오래된 순
    List<ColumnComment> findByColumnPostAndDeletedFalseOrderByIdAsc(ColumnPost columnPost);

    Optional<ColumnComment> findByIdAndDeletedFalse(Long id);
}
