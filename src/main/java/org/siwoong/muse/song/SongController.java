package org.siwoong.muse.song;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.song.service.SongReviewCommentService;
import org.siwoong.muse.song.service.SongReviewService;
import org.siwoong.muse.song.service.SongService;
import org.siwoong.muse.user.Status;
import org.siwoong.muse.user.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;
    private final SongReviewService songReviewService;
    private final SongReviewCommentService songReviewCommentService;

    public static final String LOGIN_USER_SESSION_KEY = "LOGIN_USER";

    @GetMapping
    public String list(Model model) {
        model.addAttribute("songs", songService.getAllSongs());
        return "song/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
        HttpSession session,
        Model model) {
        var song = songService.getSong(id);
        model.addAttribute("song", song);

        var summary = songService.getRatingSummary(id);
        model.addAttribute("ratingSummary", summary);

        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser != null && loginUser.getStatus() != Status.BANNED) {
            songService.getUserRatingForSong(loginUser.getId(), id)
                .ifPresent(r -> model.addAttribute("myRating", r));
        }

        // 리뷰 목록
        var reviews = songReviewService.getReviewsForSong(id);
        model.addAttribute("reviews", reviews);

        // ★ 리뷰별 댓글 Map
        var commentsByReview = songReviewCommentService.getCommentsGroupedByReview(reviews);
        model.addAttribute("commentsByReview", commentsByReview);

        // ★ 오디오 특성 요약 추가
        var audioSummary = songService.buildAudioSummary(song);
        model.addAttribute("audioSummary", audioSummary);

        return "song/detail";
    }


    // --- 별점 생략 (이미 있음) ---

    @PostMapping("/{id}/rating")
    public String rateSong(@PathVariable Long id,
        @RequestParam("score") float score,
        HttpSession session) {
        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getStatus() == Status.BANNED) return "redirect:/";

        songService.rateSong(loginUser.getId(), id, score);
        return "redirect:/songs/" + id;
    }

    @PostMapping("/{id}/rating/delete")
    public String deleteRating(@PathVariable Long id,
        HttpSession session) {
        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getStatus() == Status.BANNED) return "redirect:/";

        songService.deleteRating(loginUser.getId(), id);
        return "redirect:/songs/" + id;
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable Long id,
        @RequestParam("content") String content,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getStatus() == Status.BANNED) return "redirect:/";

        try {
            songReviewService.addReview(loginUser.getId(), id, content);
        } catch (ResponseStatusException e) {
            // 이미 리뷰 작성한 경우에만 alert 띄우고 리다이렉트
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST
                && e.getReason() != null
                && e.getReason().contains("이미 리뷰를 작성했습니다")) {

                // Flash attribute로 한 번만 전달
                redirectAttributes.addFlashAttribute("reviewAlreadyExists", true);
                return "redirect:/songs/" + id;
            }
            // 나머지 에러는 기존대로 터뜨림 (내용 비었을 때 등)
            throw e;
        }

        return "redirect:/songs/" + id;
    }

    // --- 리뷰 수정 ---
    @PostMapping("/{songId}/reviews/{reviewId}/edit")
    public String editReview(@PathVariable Long songId,
        @PathVariable Long reviewId,
        @RequestParam("content") String content,
        HttpSession session) {

        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getStatus() == Status.BANNED) return "redirect:/";

        songReviewService.editReview(loginUser.getId(), reviewId, content);
        return "redirect:/songs/" + songId;
    }

    // --- 리뷰 삭제 ---
    @PostMapping("/{songId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long songId,
        @PathVariable Long reviewId,
        HttpSession session) {

        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getStatus() == Status.BANNED) return "redirect:/";

        songReviewService.deleteReview(loginUser.getId(), reviewId);
        return "redirect:/songs/" + songId;
    }

    // 댓글 작성
    @PostMapping("/{songId}/reviews/{reviewId}/comments")
    public String addComment(@PathVariable Long songId,
        @PathVariable Long reviewId,
        @RequestParam("content") String content,
        HttpSession session) {

        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getStatus() == Status.BANNED) return "redirect:/";

        songReviewCommentService.addComment(loginUser.getId(), reviewId, content);
        return "redirect:/songs/" + songId;
    }

    // 댓글 삭제
    @PostMapping("/{songId}/reviews/{reviewId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long songId,
        @PathVariable Long reviewId,
        @PathVariable Long commentId,
        HttpSession session) {

        UserDto.Response loginUser = (UserDto.Response) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (loginUser == null) return "redirect:/login";
        if (loginUser.getStatus() == Status.BANNED) return "redirect:/";

        songReviewCommentService.deleteComment(loginUser.getId(), commentId);
        return "redirect:/songs/" + songId;
    }

}
