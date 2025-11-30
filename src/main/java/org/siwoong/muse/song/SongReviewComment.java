package org.siwoong.muse.song;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import org.siwoong.muse.common.BaseEntity;
import org.siwoong.muse.user.User;

@Entity
@Table(name = "song_review_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongReviewComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 리뷰에 달린 댓글인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private SongReview review;

    // 댓글 작성자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "has_profanity", nullable = false)
    private boolean hasProfanity = false;

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void softDelete() {
        this.deleted = true;
    }

}
