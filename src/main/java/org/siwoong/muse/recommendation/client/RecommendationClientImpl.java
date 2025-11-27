package org.siwoong.muse.recommendation.client;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.siwoong.muse.recommendation.dto.PersonalRecResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationClientImpl implements RecommendationClient {

    private final RestTemplate restTemplate;

    @Value("${recommender.base-url}")
    private String baseUrl;


    @Override
    public PersonalRecResponseDto getPersonalRecommendations(Long userId, int topN) {
        String url = UriComponentsBuilder
            .fromHttpUrl(baseUrl)
            .path("/recommend/personal")
            .queryParam("user_id", userId)
            .queryParam("top_n", topN)
            .toUriString();

        try {
            PersonalRecResponseDto response =
                restTemplate.getForObject(url, PersonalRecResponseDto.class);

            if (response == null) {
                log.warn("Recommendation API returned null for user {}", userId);
                PersonalRecResponseDto empty = new PersonalRecResponseDto();
                empty.setUserId(userId);
                empty.setRecommendations(java.util.List.of());
                return empty;
            }
            return response;
        } catch (Exception e) {
            log.error("Failed to call recommendation API. url={}", url, e);

            // 실패 시에도 NPE 안 나도록 빈 결과 반환
            PersonalRecResponseDto fallback = new PersonalRecResponseDto();
            fallback.setUserId(userId);
            fallback.setRecommendations(java.util.List.of());
            return fallback;
        }
    }
}
