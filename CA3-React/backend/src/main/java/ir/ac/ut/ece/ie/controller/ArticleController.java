package ir.ac.ut.ece.ie.controller;

import java.util.*;
import ir.ac.ut.ece.ie.dto.*;
import org.springframework.http.HttpStatus;
import ir.ac.ut.ece.ie.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public List<ArticleSummaryDto> listArticles(@RequestParam(required = false) String query, @RequestParam(defaultValue = "refs") String sortBy) {
        return articleService.getArticles(query, sortBy);
    }

    @GetMapping("/{id}")
    public ArticleDetailDto getArticle(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody ArticleRequestDto dto) {
        try {
            ArticleDetailDto created = articleService.createArticle(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }
        catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody ArticleRequestDto dto) {
        try {
            ArticleDetailDto updated = articleService.updateArticle(id, dto);
            return ResponseEntity.ok(updated);
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        }
    }
}
