package org.siwoong.muse.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.song.repository.SongReviewRepository;
import org.siwoong.muse.user.FollowService;
import org.siwoong.muse.user.User;
import org.siwoong.muse.user.UserDto;
import org.siwoong.muse.user.UserService;
import org.siwoong.muse.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserProfileController {

    private final UserRepository userRepository;
    private final FollowService followService;
    private final UserService userService;
    private final SongReviewRepository songReviewRepository;

    private static final String LOGIN_USER_SESSION_KEY = "LOGIN_USER";

    // 내 프로필로 이동
    @GetMapping("/me")
    public String me(HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) {
            return "redirect:/login";
        }
        return "redirect:/users/" + loginUser.getId();
    }

    // 유저 프로필
    @GetMapping("/{id}")
    public String profile(@PathVariable Long id,
        HttpSession session,
        Model model) {

        User target = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        Long viewerId = (loginUser != null) ? loginUser.getId() : null;
        boolean isMyProfile = (viewerId != null && viewerId.equals(id));
        boolean isFollowing = (viewerId != null && followService.isFollowing(viewerId, id));

        long followerCount = followService.countFollowers(id);
        long followingCount = followService.countFollowing(id);

        var recentReviews = songReviewRepository
            .findTop20ByUser_IdAndDeletedFalseOrderByCreatedAtDesc(id);

        model.addAttribute("targetUser", target);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("isMyProfile", isMyProfile);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("recentReviews", recentReviews);

        return "user/profile";
    }

    // 팔로우
    @PostMapping("/{id}/follow")
    public String follow(@PathVariable Long id, HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null) return "redirect:/login";

        followService.follow(loginUser.getId(), id);
        return "redirect:/users/" + id;
    }

    // 언팔로우
    @PostMapping("/{id}/unfollow")
    public String unfollow(@PathVariable Long id, HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null) return "redirect:/login";

        followService.unfollow(loginUser.getId(), id);
        return "redirect:/users/" + id;
    }


    @GetMapping("/{id}/following")
    public String following(@PathVariable Long id,
        HttpSession session,
        Model model) {

        User target = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        var followingList = followService.getFollowing(id); // List<Follow>

        model.addAttribute("targetUser", target);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("followingList", followingList);

        return "user/following";
    }

    @GetMapping("/{id}/followers")
    public String followers(@PathVariable Long id,
        HttpSession session,
        Model model) {

        User target = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        var followerList = followService.getFollowers(id); // List<Follow>

        model.addAttribute("targetUser", target);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("followerList", followerList);

        return "user/followers";
    }

    // 🔹 프로필 수정 폼
    @GetMapping("/me/edit")
    public String editForm(HttpSession session, Model model) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null) {
            return "redirect:/login";
        }

        User user = userRepository.findById(loginUser.getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
            ));

        model.addAttribute("user", user); // 현재 값들을 폼에 채우기 위함
        model.addAttribute("errorMessage", null);
        return "user/edit";
    }

    @PostMapping("/me/edit")
    public String edit(@RequestParam String nickname,
        @RequestParam(required = false) String description,
        HttpSession session,
        Model model) {

        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null) {
            return "redirect:/login";
        }

        String trimmedNickname = nickname == null ? "" : nickname.trim();
        String trimmedDescription = (description == null) ? "" : description.trim();

        // 현재 유저 엔티티 로드
        User user = userRepository.findById(loginUser.getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."
            ));

        // 1) 닉네임 비어있으면
        if (trimmedNickname.isEmpty()) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "닉네임은 비워둘 수 없습니다.");
            return "user/edit";
        }

        // 2) 닉네임 중복 체크 (자기 자신 제외)
        boolean nicknameTaken =
            userRepository.existsByNicknameAndIdNot(trimmedNickname, user.getId());

        if (nicknameTaken) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "이미 사용 중인 닉네임입니다.");
            return "user/edit";
        }

        // 3) 실제 업데이트 (여기서 영속성 사용)
        User updated = userService.updateProfile(user.getId(), trimmedNickname, trimmedDescription);

        // 4) 세션 갱신
        UserDto.Response newSessionUser = new UserDto.Response(updated);
        session.setAttribute(LOGIN_USER_SESSION_KEY, newSessionUser);

        // 5) 내 프로필로 리다이렉트
        return "redirect:/users/" + updated.getId();
    }

}
