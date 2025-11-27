package org.siwoong.muse.column;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ColumnPostRepository extends JpaRepository<ColumnPost, Long> {

    // 기본 조회 (soft delete 고려)
    Optional<ColumnPost> findByIdAndDeletedFalse(Long id);

    List<ColumnPost> findTop20ByDeletedFalseOrderByCreatedAtDesc();

    // 🔍 제목 또는 내용에 keyword 포함 (삭제 안 된 것만)
    @Query("""
           select c
           from ColumnPost c
           where c.deleted = false
             and (
               lower(c.title) like lower(concat('%', :keyword, '%'))
               or lower(c.content) like lower(concat('%', :keyword, '%'))
             )
           order by c.createdAt desc
           """)
    List<ColumnPost> searchByKeyword(@Param("keyword") String keyword);

    // 삭제 안 된 칼럼들, 최신순
    List<ColumnPost> findTop50ByDeletedFalseOrderByCreatedAtDesc();

}
