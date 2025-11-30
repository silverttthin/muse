package org.siwoong.muse.admin.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProfanityItemView {

    public enum ContentType {
        SONG_REVIEW,
        SONG_REVIEW_COMMENT,
        COLUMN_COMMENT
    }

    private ContentType type;
    private Long id;              // 해당 엔티티 id
    private String snippet;       // 내용 일부
    private String authorNickname;
    private LocalDateTime createdAt;
    private String targetLink;    // 관리자용 "원문 보기" 링크


}
