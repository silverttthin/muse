package org.siwoong.muse.recommendation.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalRecResponseDto {

    @JsonProperty("user_id")
    private Long userId;

    private List<RecommendationItemDto> recommendations;

}
