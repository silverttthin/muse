// RecommendationService.java
package org.siwoong.muse.recommendation;

import lombok.RequiredArgsConstructor;
import org.siwoong.muse.recommendation.client.RecommendationClient;
import org.siwoong.muse.recommendation.dto.PersonalRecResponseDto;
import org.siwoong.muse.recommendation.dto.RecommendationItemDto;
import org.siwoong.muse.recommendation.dto.RecommendedSongView;
import org.siwoong.muse.song.Song;
import org.siwoong.muse.song.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationClient recommendationClient;
    private final SongRepository songRepository;

    public List<RecommendedSongView> getPersonalRecommendations(Long userId, int topN) {
        PersonalRecResponseDto response =
            recommendationClient.getPersonalRecommendations(userId, topN);

        List<RecommendationItemDto> recItems = response.getRecommendations();
        if (recItems == null || recItems.isEmpty()) {
            return List.of();
        }

        // 1) 추천된 songId 목록
        List<Long> songIdsInOrder = recItems.stream()
            .map(RecommendationItemDto::getSongId)
            .collect(Collectors.toList());

        // 2) 한번에 Song 엔티티 쿼리
        List<Song> songs = songRepository.findSongsByIdIn(songIdsInOrder);

        // 3) id -> Song 매핑
        Map<Long, Song> songMap = songs.stream()
            .collect(Collectors.toMap(Song::getId, s -> s));

        // 4) score 정보도 map으로
        Map<Long, Double> scoreMap = recItems.stream()
            .collect(Collectors.toMap(
                RecommendationItemDto::getSongId,
                RecommendationItemDto::getScore
            ));

        // 5) 추천 순서 유지하면서 View DTO 리스트 생성
        List<RecommendedSongView> result = new ArrayList<>();

        for (Long songId : songIdsInOrder) {
            Song song = songMap.get(songId);
            if (song == null) {
                continue; // DB에 없으면 스킵
            }
            RecommendedSongView view = new RecommendedSongView();
            view.setSongId(song.getId());
            view.setTitle(song.getTitle());
            view.setArtist(song.getArtist());
            view.setScore(scoreMap.getOrDefault(songId, 0.0));
            view.setAlbumImageUrl(song.getAlbumImageUrl());
            view.setArtistDetailUrl(song.getArtistDetailUrl());
            result.add(view);
        }

        return result;
    }


    public List<RecommendedSongView> getHomeRecommendations(Long userId) {
        // 1) FastAPI에서 상위 50곡 받아오기
        List<RecommendedSongView> top50 = getPersonalRecommendations(userId, 50);
        if (top50.isEmpty()) {
            // 콜드스타트 혹은 추천 불가
            return List.of();
        }

        // 2) 매 요청마다 랜덤 섞기
        Collections.shuffle(top50);

        // 3) 상위 10개만 잘라서 사용
        int size = Math.min(10, top50.size());
        return top50.subList(0, size);
    }

}
