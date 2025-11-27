package org.siwoong.muse.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationItemDto {
    @JsonProperty("song_id")
    private Long songId;
    private double score;
}
