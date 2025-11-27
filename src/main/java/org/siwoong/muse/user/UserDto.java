package org.siwoong.muse.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;


public class UserDto {

    // 인스턴스화 방지 (껍데기 클래스이므로)
    private UserDto() {}

    // 1. 회원가입 요청
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Builder
    public static class SignUpRequest {
        private String email;
        private String password;
        private String nickname;

        // 필요하다면 toEntity() 메서드를 여기에 추가해서 깔끔하게 관리 가능
    }

    // 2. 로그인 요청
    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    // 3. 유저 응답
    // 이름이 UserResponse였지만, UserDto.Response로 쓰면 되므로 'Response'로 줄여도 됩니다.
    @Getter
    public static class Response {
        private final Long id;
        private final String email;
        private final String nickname;
        private final String description;
        private final Role role;
        private final Status status;

        // Entity -> DTO 변환 생성자
        public Response(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.description = user.getDescription();
            this.role = user.getRole();
            this.status = user.getStatus();
        }

        // (선택) 정적 팩토리 메서드 패턴을 선호한다면
        public static Response from(User user) {
            return new Response(user);
        }
    }
}
