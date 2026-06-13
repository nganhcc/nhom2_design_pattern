package com.group.videosharing.controller;

import com.group.videosharing.dto.VideoDto;
import com.group.videosharing.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TV1 — SearchContext, ContentLoader
 */
@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String q,
                                    @RequestParam(required = false, defaultValue = "relevance") String sort,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) Integer minDuration,
                                    @RequestParam(required = false) Integer maxDuration,
                                    @RequestParam(required = false) String from,
                                    @RequestParam(required = false) String to) {
        try {
            List<VideoDto> results = searchService.search(q, sort, category, minDuration, maxDuration, from, to);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/api/home")
    public ResponseEntity<?> home(@RequestParam(required = false) String viewerId) {
        try {
            return ResponseEntity.ok(searchService.home(viewerId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

}
