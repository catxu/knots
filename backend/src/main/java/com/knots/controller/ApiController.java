package com.knots.controller;

import com.github.pagehelper.PageInfo;
import com.knots.dto.KnotCategoryDTO;
import com.knots.entity.Knot;
import com.knots.service.KnotService;
import com.knots.web.form.KnotQueryForm;
import com.knots.web.vo.KnotVO;
import com.oak.root.web.result.WebPageableResult;
import com.oak.root.web.result.WebQueryResult;
import com.oak.root.web.result.WebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private static final int POPULAR_LIMIT = 6;
    @Autowired
    private KnotService knotService;

    @GetMapping("/categories")
    public WebQueryResult<KnotCategoryDTO> getCategories() {
        List<KnotCategoryDTO> categories = knotService.getAllCategories();
        return WebQueryResult.successResult(categories);
    }

    @GetMapping("/search")
    public WebPageableResult<KnotVO> searchKnots(KnotQueryForm form) {
        PageInfo<KnotVO> pageInfo = knotService.searchKnots(form);
        return WebPageableResult.successResult(form.getPage(), pageInfo.getTotal(), pageInfo.getList());
    }

    @GetMapping("/knots")
    public WebPageableResult<KnotVO> getKnots(KnotQueryForm form) {
        Page<Knot> knots;
        if (categoryId != null) {
            knots = knotService.getKnotsByCategory(categoryId, page, size);
        } else {
            knots = knotService.getAllPublishedKnots(page, size);
        }
        return ResponseEntity.ok(knots);
    }

    @GetMapping("/knots/{id}")
    public WebResult<KnotVO> getKnotById(@PathVariable Long id) {
        Knot knot = knotService.getKnotById(id);
        if (knot != null) {
            knotService.incrementViewCount(id);
            return ResponseEntity.ok(knot);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/knots/popular")
    public WebQueryResult<KnotVO> getPopularKnots() {
        List<KnotVO> knots = knotService.getTopViewedKnots(POPULAR_LIMIT);
        return ResponseEntity.ok(knots);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("绳结教程API服务运行正常！");
    }
}
