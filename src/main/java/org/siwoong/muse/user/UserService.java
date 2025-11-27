package org.siwoong.muse.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public UserDto.Response signUp(UserDto.SignUpRequest request) {
        // 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
            .email(request.getEmail())
            .passwordHash(encodedPassword)   // 인코딩된 비밀번호 저장
            .nickname(request.getNickname())
            .build();

        User saved = userRepository.save(user);
        return new UserDto.Response(saved);
    }

    // 로그인
    @Transactional(readOnly = true)
    public UserDto.Response login(UserDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.")
            );

        // 비밀번호 검증 (matches 사용)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        if (user.getStatus() == Status.BANNED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "차단된 계정입니다.");
        }

        return new UserDto.Response(user);
    }

    public User updateProfile(Long userId, String nickname, String description) {
        User user = userRepository.findById(userId)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")
            );

        String trimmedNickname = nickname == null ? "" : nickname.trim();
        String trimmedDescription = (description == null) ? "" : description.trim();

        if (trimmedNickname.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "닉네임은 비워둘 수 없습니다.");
        }

        user.updateProfile(trimmedNickname, trimmedDescription);
        return user; // 영속 엔티티라 save() 안 해도 flush 시점에 반영됨
    }

    // ID로 유저 조회
    @Transactional(readOnly = true)
    public UserDto.Response findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.")
            );
        return new UserDto.Response(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void updateUserRoleAndStatus(Long userId, Role newRole, Status newStatus) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
            ));

        user.setRole(newRole);
        user.setStatus(newStatus);
        // 영속 엔티티라 save() 없어도 트랜잭션 끝날 때 업데이트 된다.
    }
}
