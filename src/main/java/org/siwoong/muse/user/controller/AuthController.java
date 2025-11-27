package org.siwoong.muse.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.user.UserDto;
import org.siwoong.muse.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    public static final String LOGIN_USER_SESSION_KEY = "LOGIN_USER";

    private final UserService userService;

    // 회원가입 폼
    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("signUpRequest", UserDto.SignUpRequest.builder().build());
        return "auth/signup"; // templates/auth/signup.html
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signUp(@ModelAttribute("signUpRequest") UserDto.SignUpRequest request,
        Model model) {
        try {
            userService.signUp(request);
            // 성공하면 로그인 페이지로
            return "redirect:/login";
        } catch (Exception e) {
            // ResponseStatusException이면 message 그대로 쓰고,
            // 그 외에는 일반 에러 처리
            model.addAttribute("error", e.getMessage());
            return "auth/signup";
        }
    }

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new UserDto.LoginRequest());
        return "auth/login"; // templates/auth/login.html
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequest") UserDto.LoginRequest request,
        HttpSession session,
        Model model) {
        try {
            UserDto.Response loginUser = userService.login(request);

            // 세션에 로그인 유저 정보 저장
            session.setAttribute(LOGIN_USER_SESSION_KEY, loginUser);

            return "redirect:/"; // 홈으로
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
