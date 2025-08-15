package com.knots.controller;

import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.repository.KnotCategoryRepository;
import com.knots.service.KnotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private KnotCategoryRepository categoryRepository;

    @Autowired
    private KnotService knotService;

    @GetMapping("/categories")
    public ResponseEntity<List<KnotCategory>> getCategories() {
        List<KnotCategory> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Knot>> searchKnots(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Knot> knots = knotService.searchKnots(keyword, pageable);
        return ResponseEntity.ok(knots);
    }

    @GetMapping("/knots")
    public ResponseEntity<Page<Knot>> getKnots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId) {
        Page<Knot> knots;
        if (categoryId != null) {
            knots = knotService.getKnotsByCategory(categoryId, page, size);
        } else {
            knots = knotService.getAllPublishedKnots(page, size);
        }
        return ResponseEntity.ok(knots);
    }

    @GetMapping("/knots/{id}")
    public ResponseEntity<Knot> getKnotById(@PathVariable Long id) {
        Knot knot = knotService.getKnotById(id);
        if (knot != null) {
            knotService.incrementViewCount(id);
            return ResponseEntity.ok(knot);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/knots/popular")
    public ResponseEntity<List<Knot>> getPopularKnots(
            @RequestParam(defaultValue = "5") int limit) {
        List<Knot> knots = knotService.getTopViewedKnots(limit);
        return ResponseEntity.ok(knots);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("绳结教程API服务运行正常！");
    }
}
