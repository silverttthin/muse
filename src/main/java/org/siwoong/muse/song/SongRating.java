package org.siwoong.muse.song;


import jakarta.persistence.*;
import lombok.*;
import org.siwoong.muse.user.User;

@Entity
@Table(
    name = "song_ratings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_song_rating_user_song",
            columnNames = {"user_id", "song_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 별점 준 유저
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 별점 대상 노래
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    // 0.5 ~ 5.0 이런 식으로 쓸 예정이면 Float/Double
    @Column(nullable = false)
    private Float score;
}
