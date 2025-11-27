package org.siwoong.muse.recommendation.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendedSongView {
    private Long songId;
    private String title;
    private String artist;
    private double score;

    // image도 추가하기

}
