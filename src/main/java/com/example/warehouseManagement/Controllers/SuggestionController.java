package com.example.warehouseManagement.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.warehouseManagement.Services.SuggestionService;
import com.example.warehouseManagement.Services.SuggestionService.Mode;
import com.example.warehouseManagement.Services.SuggestionService.Suggestion;
import com.example.warehouseManagement.Services.SuggestionService.Type;

/**
 * JSON endpoint that powers the autocomplete dropdown. Frontend calls it
 * with the partial query, the suggestion category, and the desired match
 * mode (prefix or contains). Returns at most {@code limit} matches.
 */
@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping
    public List<Suggestion> suggest(@RequestParam("q") String query,
                                    @RequestParam("type") Type type,
                                    @RequestParam(value = "mode", defaultValue = "PREFIX") Mode mode,
                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        int capped = Math.max(1, Math.min(limit <= 0 ? DEFAULT_LIMIT : limit, MAX_LIMIT));
        return suggestionService.suggest(type, mode, query, capped);
    }
}
