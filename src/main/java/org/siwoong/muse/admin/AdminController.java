package org.siwoong.muse.admin;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.user.Role;
import org.siwoong.muse.user.Status;
import org.siwoong.muse.user.User;
import org.siwoong.muse.user.UserDto;
import org.siwoong.muse.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ModerationService moderationService;

    private static final String LOGIN_USER_SESSION_KEY = "LOGIN_USER";

    // 공통: ADMIN 체크 헬퍼
    private UserDto.Response getAdminOrRedirect(HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) {
            return null;
        }
        if (loginUser.getRole() != Role.ADMIN) {
            return null;
        }
        return loginUser;
    }

    // /admin → /admin/users로 리다이렉트
    @GetMapping
    public String adminHome(HttpSession session) {
        UserDto.Response admin = getAdminOrRedirect(session);
        if (admin == null) {
            return "redirect:/"; // 권한 없으면 홈으로
        }
        return "redirect:/admin/users";
    }

    // 유저 목록
    @GetMapping("/users")
    public String userList(HttpSession session, Model model) {
        UserDto.Response admin = getAdminOrRedirect(session);
        if (admin == null) {
            return "redirect:/";
        }

        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("loginUser", admin);
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", Status.values());

        return "admin/users";
    }

    // 특정 유저의 role/status 변경
    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
        @RequestParam("role") String roleValue,
        @RequestParam("status") String statusValue,
        HttpSession session) {
        UserDto.Response admin = getAdminOrRedirect(session);
        if (admin == null) {
            return "redirect:/";
        }

        Role newRole = Role.valueOf(roleValue);
        Status newStatus = Status.valueOf(statusValue);

        // 자기 자신 BAN 방지 정도는 하고 싶으면 여기서 체크해도 됨
        // if (admin.getId().equals(id) && newStatus == Status.BANNED) { ... }

        userService.updateUserRoleAndStatus(id, newRole, newStatus);

        return "redirect:/admin/users";
    }

    @GetMapping("/profanity")
    public String profanityList(HttpSession session, Model model) {
        UserDto.Response admin = getAdminOrRedirect(session);
        if (admin == null) {
            return "redirect:/";
        }

        model.addAttribute("loginUser", admin);
        model.addAttribute("items", moderationService.getAllFlaggedContent());

        return "admin/profanity";
    }
}
