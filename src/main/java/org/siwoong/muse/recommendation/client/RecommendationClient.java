package org.siwoong.muse.recommendation.client;

import org.siwoong.muse.recommendation.dto.PersonalRecResponseDto;

public interface RecommendationClient {
    PersonalRecResponseDto getPersonalRecommendations(Long userId, int topN);
}
