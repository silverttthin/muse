package org.siwoong.muse.column;


import jakarta.persistence.*;
import lombok.*;

import org.siwoong.muse.common.BaseEntity;
import org.siwoong.muse.user.User;

@Entity
@Table(name = "columns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 (보통 CURATOR role)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean deleted = false;

    public void update(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
    }

    public void softDelete() {
        this.deleted = true;
    }

}
