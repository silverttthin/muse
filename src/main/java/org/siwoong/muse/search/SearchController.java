package org.siwoong.muse.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public String search(@RequestParam(name = "query", required = false) String query,
        Model model) {

        if (query == null || query.isBlank()) {
            model.addAttribute("query", "");
            model.addAttribute("emptyQuery", true);
            return "search/results";
        }

        var result = searchService.search(query);

        model.addAttribute("query", query);
        model.addAttribute("emptyQuery", false);
        model.addAttribute("songs", result.songs());
        model.addAttribute("users", result.users());
        model.addAttribute("columns", result.columns());

        return "search/results";
    }
}
