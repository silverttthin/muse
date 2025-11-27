package org.siwoong.muse.column;

import lombok.RequiredArgsConstructor;
import org.siwoong.muse.user.Role;
import org.siwoong.muse.user.Status;
import org.siwoong.muse.user.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/columns")
public class ColumnController {

    private final ColumnService columnService;
    private final ColumnCommentService columnCommentService;

    // 네가 쓰는 세션 키에 맞춰 수정
    private static final String LOGIN_USER_SESSION_KEY = "LOGIN_USER";

    // 목록
    @GetMapping
    public String list(Model model, HttpSession session) {
        model.addAttribute("columns", columnService.getLatestColumns());
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        model.addAttribute("loginUser", loginUser);
        return "column/list";
    }

    // 상세
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
        HttpSession session,
        Model model) {
        var column = columnService.getColumn(id);
        var comments = columnCommentService.getCommentsForColumn(id);

        model.addAttribute("column", column);
        model.addAttribute("comments", comments);

        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        model.addAttribute("loginUser", loginUser);

        return "column/detail";
    }

    // 작성 폼 (CURATOR/ADMIN만)
    @GetMapping("/new")
    public String newForm(HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null) {
            return "redirect:/login";
        }
        if (!(loginUser.getRole() == Role.CURATOR || loginUser.getRole() == Role.ADMIN)) {
            return "redirect:/columns"; // 권한 없으면 목록으로
        }

        return "column/new";
    }

    // 작성 처리
    @PostMapping
    public String create(@RequestParam String title,
        @RequestParam String content,
        HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null || loginUser.getStatus() == Status.BANNED) {
            return "redirect:/login";
        }

        var post = columnService.createColumn(loginUser.getId(), title, content);
        return "redirect:/columns/" + post.getId();
    }

    // 삭제 (ADMIN만)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
        HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null) {
            return "redirect:/login";
        }

        columnService.deleteColumn(loginUser.getId(), id);
        return "redirect:/columns";
    }

    // 댓글 작성
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
        @RequestParam String content,
        HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null || loginUser.getStatus() == Status.BANNED) {
            return "redirect:/login";
        }

        columnCommentService.addComment(loginUser.getId(), id, content);
        return "redirect:/columns/" + id;
    }

    // 댓글 삭제
    @PostMapping("/{columnId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long columnId,
        @PathVariable Long commentId,
        HttpSession session) {
        UserDto.Response loginUser =
            (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);

        if (loginUser == null) {
            return "redirect:/login";
        }

        columnCommentService.deleteComment(loginUser.getId(), commentId);
        return "redirect:/columns/" + columnId;
    }
}
