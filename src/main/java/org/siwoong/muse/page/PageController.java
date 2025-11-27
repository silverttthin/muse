package org.siwoong.muse.page;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.siwoong.muse.recommendation.RecommendationService;
import org.siwoong.muse.recommendation.dto.RecommendedSongView;
import org.siwoong.muse.song.service.SongService;
import org.siwoong.muse.user.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class PageController {

    // 나중에 홈에 보여줄 데이터가 생기면 서비스 주입해서 사용하면 됨
     private final SongService songService;
     private final RecommendationService recommendationService;
    // private final ColumnService columnService;

    // 홈 화면
    @GetMapping("/")
    public String home(
        Model model,
        @SessionAttribute(name = "LOGIN_USER", required = false) UserDto.Response loginUser
    ) {
        // 1) 비로그인: 그냥 index.html에서 session.LOGIN_USER 기준으로 로그인 안내만 보여주면 됨
        if (loginUser == null) {
            return "index";
        }

        // 2) 로그인 상태: 추천 엔진 호출 (top50 → 랜덤 10곡)
        List<RecommendedSongView> recs =
            recommendationService.getHomeRecommendations(loginUser.getId());

        boolean coldStart = recs.isEmpty();
        model.addAttribute("coldStart", coldStart);
        if (!coldStart) {
            model.addAttribute("recommendations", recs);
        }

        return "index";
    }

    // 마이페이지 (로그인 필요, 나중에 인터셉터로 보호)
    @GetMapping("/mypage")
    public String myPage(Model model) {
        // 세션에서 LOGIN_USER 읽어서 뭔가 보여주고 싶으면
        // 컨트롤러에서 세션 꺼내도 되고, 템플릿에서 session.LOGIN_USER 써도 됨.
        return "user/mypage"; // templates/user/mypage.html
    }
}
