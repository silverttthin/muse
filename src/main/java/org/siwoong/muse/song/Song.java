package org.siwoong.muse.song;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Getter;
import org.hibernate.annotations.Immutable;


@Entity
@Table(
    name = "songs",
    indexes = {
        @Index(name = "idx_songs_title", columnList = "title"),
        @Index(name = "idx_songs_artist", columnList = "artist"),
        @Index(name = "idx_songs_spotify_id", columnList = "spotify_id", unique = true),
        @Index(name = "idx_songs_playlist_genre", columnList = "playlist_genre"),
        @Index(name = "idx_songs_energy", columnList = "energy"),
        @Index(name = "idx_songs_tempo", columnList = "tempo"),
        @Index(name = "idx_songs_danceability", columnList = "danceability"),
        @Index(name = "idx_songs_valence", columnList = "valence"),
        @Index(name = "idx_songs_album_image_url", columnList = "album_image_url"),
        @Index(name = "idx_songs_artist_detail_url", columnList = "artist_detail_url")
    }
)
@Getter
@Immutable
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "artist", nullable = false, length = 255)
    private String artist;

    @Column(name = "spotify_id", nullable = false, length = 100)
    private String spotifyId;

    @Column(name = "playlist_genre", length = 100)
    private String playlistGenre;

    @Column(name = "energy")
    private Float energy;

    @Column(name = "tempo")
    private Float tempo;

    @Column(name = "danceability")
    private Float danceability;

    @Column(name = "loudness")
    private Float loudness;

    @Column(name = "valence")
    private Float valence;

    @Column(name = "speechiness")
    private Float speechiness;

    @Column(name = "instrumentalness")
    private Float instrumentalness;

    @Column(name = "acousticness")
    private Float acousticness;

    @Column(name = "time_signature")
    private Integer timeSignature;

    @Column(name = "mode")
    private Integer mode;

    @Column(name = "key")
    private Integer key;

    @Column(name = "duration_ms")
    private Float durationMs;

    @Column(name = "album_image_url")
    private String albumImageUrl;

    @Column(name = "artist_detail_url")
    private String artistDetailUrl;
}
