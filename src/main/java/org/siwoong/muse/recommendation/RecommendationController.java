// RecommendationController.java
package org.siwoong.muse.recommendation;

import lombok.RequiredArgsConstructor;
import org.siwoong.muse.recommendation.dto.RecommendedSongView;
import org.siwoong.muse.recommendation.RecommendationService;
import org.siwoong.muse.user.UserDto; // 실제 경로/타입명 맞게 수정
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/recommendations")
    public String recommendations(
        @SessionAttribute("LOGIN_USER") UserDto.Response loginUser, // 네 프로젝트 타입명에 맞게
        Model model
    ) {
        Long userId = loginUser.getId();
        List<RecommendedSongView> recs =
            recommendationService.getPersonalRecommendations(userId, 30);

        model.addAttribute("recommendations", recs);
        return "recommendations/list"; // templates/recommendations/list.html
    }
}
