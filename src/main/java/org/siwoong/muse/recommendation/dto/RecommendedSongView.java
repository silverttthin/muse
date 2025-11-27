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
    private String albumImageUrl;
    private String artistDetailUrl; // 나중에 상세 페이지에서 쓰고 싶으면 같이 넣자

}
