package org.siwoong.muse.song;

import jakarta.persistence.*;
import lombok.*;

import org.siwoong.muse.common.BaseEntity;
import org.siwoong.muse.user.User;

@Entity
@Table(
    name = "song_reviews",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_song_review_user_song",
            columnNames = {"user_id", "song_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 리뷰 대상 노래
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 소프트 삭제 플래그
    @Column(nullable = false)
    private boolean deleted = false;

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void softDelete() {
        this.deleted = true;
    }

}
