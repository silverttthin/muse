package org.siwoong.muse.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("일반 사용자"),
    CURATOR("큐레이터"),
    ADMIN("관리자");

    private final String title;
}
