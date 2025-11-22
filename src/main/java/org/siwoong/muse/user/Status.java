package org.siwoong.muse.user;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ACTIVE("활동 중"),
    BANNED("정지");

    private final String statusName;
}
