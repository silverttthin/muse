package org.siwoong.muse.song.service;

import lombok.RequiredArgsConstructor;
import org.siwoong.muse.song.Song;
import org.siwoong.muse.song.SongRating;
import org.siwoong.muse.song.repository.SongRepository;
import org.siwoong.muse.song.repository.SongRatingRepository;

import org.siwoong.muse.user.User;
import org.siwoong.muse.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongService {

    private final SongRepository songRepository;
    private final SongRatingRepository songRatingRepository;
    private final UserRepository userRepository;

    public List<Song> getAllSongs() {
        return songRepository.findTop50ByOrderByIdAsc();
    }

    public Song getSong(Long id) {
        return songRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 곡을 찾을 수 없습니다."));
    }

    // --- 별점 관련 ---

    public Optional<SongRating> getUserRatingForSong(Long userId, Long songId) {
        return songRatingRepository.findByUserIdAndSongId(userId, songId);
    }

    public RatingSummary getRatingSummary(Long songId) {
        Double avg = songRatingRepository.findAverageScoreBySongId(songId);
        long count = songRatingRepository.countBySongId(songId);

        double avgValue = (avg != null) ? avg : 0.0;

        return new RatingSummary(avgValue, count);
    }


    @Transactional
    public void rateSong(Long userId, Long songId, float score) {
        // 점수 범위 검증 (원하면 조절)
        if (score < 0.5f || score > 5.0f) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "별점은 0.5에서 5.0 사이여야 합니다.");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 곡을 찾을 수 없습니다."));

        SongRating rating = songRatingRepository.findByUserIdAndSongId(userId, songId)
            .orElseGet(() -> SongRating.builder()
                .user(user)
                .song(song)
                .score(score)
                .build());

        // 기존 있으면 score만 변경
        rating.updateScore(score);

        songRatingRepository.save(rating);
    }

    @Transactional
    public void deleteRating(Long userId, Long songId) {
        SongRating rating = songRatingRepository.findByUserIdAndSongId(userId, songId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "별점이 존재하지 않습니다."));

        if (!rating.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 별점만 삭제할 수 있습니다.");
        }

        songRatingRepository.delete(rating);
    }

    // 평균 + 개수 전달용 DTO
    public record RatingSummary(double averageScore, long ratingCount) {}

    public record AudioSummary(
        String energyText,
        String danceabilityText,
        String tempoText,
        String moodText
    ){}

    public AudioSummary buildAudioSummary(Song song) {
        return new AudioSummary(
            describeEnergy(song.getEnergy()),
            describeDanceability(song.getDanceability()),
            describeTempo(song.getTempo()),
            describeMood(song.getValence())
        );
    }

    private String describeEnergy(Float energy) {
        if (energy == null) return "에너지 정보를 알 수 없어요.";

        if (energy < 0.3f) {
            return "전체적으로 에너지가 낮은, 잔잔한 곡이에요.";
        } else if (energy < 0.6f) {
            return "적당한 에너지를 가진 곡이에요. 너무 잔잔하지도, 과하지도 않아요.";
        } else {
            return "에너지가 높은 곡이에요! 강한 비트와 다이내믹한 사운드를 기대해도 좋아요.";
        }
    }

    private String describeDanceability(Float danceability) {
        if (danceability == null) return "춤추기 좋은 정도는 알 수 없어요.";

        if (danceability < 0.3f) {
            return "춤보다는 가만히 감상하기 좋은 스타일이에요.";
        } else if (danceability < 0.6f) {
            return "리듬을 타기 좋은 편이에요. 가볍게 몸을 흔들기 좋습니다.";
        } else {
            return "춤추기 아주 좋은 노래예요! 클럽/파티 분위기와 잘 어울려요.";
        }
    }

    private String describeTempo(Float tempo) {
        if (tempo == null) return "템포 정보를 알 수 없어요.";

        // tempo는 BPM 기준 가정
        if (tempo < 90) {
            return "템포가 느린 편이에요. 잔잔하거나 감성적인 곡일 가능성이 높아요.";
        } else if (tempo < 120) {
            return "중간 정도의 템포예요. 가장 무난하게 듣기 좋은 속도입니다.";
        } else {
            return "템포가 빠른 편이에요! 신나고 속도감 있는 곡일 가능성이 커요.";
        }
    }

    private String describeMood(Float valence) {
        if (valence == null) return "곡의 분위기(밝기)는 알 수 없어요.";

        if (valence < 0.3f) {
            return "조금 어둡고 진지한 분위기의 곡이에요.";
        } else if (valence < 0.6f) {
            return "밝음/어두움이 섞인 중간 분위기예요. 감정선이 복합적인 곡일 수 있어요.";
        } else {
            return "밝고 경쾌한 분위기의 곡이에요!";
        }
    }
}
