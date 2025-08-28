package com.knots.controller;

import com.github.pagehelper.PageInfo;
import com.knots.dto.KnotCategoryDTO;
import com.knots.dto.KnotDTO;
import com.knots.service.CategoryService;
import com.knots.service.FileService;
import com.knots.service.KnotService;
import com.knots.web.form.KnotQueryForm;
import com.knots.web.vo.KnotImageVO;
import com.knots.web.vo.KnotVO;
import com.oak.root.web.result.WebPageableResult;
import com.oak.root.web.result.WebQueryResult;
import com.oak.root.web.result.WebResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private static final int POPULAR_LIMIT = 6;
    @Autowired
    private KnotService knotService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public WebQueryResult<KnotCategoryDTO> getCategories() {
        List<KnotCategoryDTO> categories = categoryService.getAllCategories();
        return WebQueryResult.successResult(categories);
    }

    @GetMapping("/search")
    public WebPageableResult<KnotVO> searchKnots(KnotQueryForm form) {
        PageInfo<KnotDTO> pageInfo = knotService.searchKnots(form);
        return WebPageableResult.successResult(form.getPage(), pageInfo.getTotal(), pageInfo.getList().stream().map(e -> {
            KnotVO t = new KnotVO();
            BeanUtils.copyProperties(e, t);
            t.setCoverImage(FileService.buildImageUrl(t.getCoverImage()));
            return t;
        }).collect(Collectors.toList()));
    }

    @GetMapping("/knots")
    public WebPageableResult<KnotVO> getKnots(KnotQueryForm form) {
        return searchKnots(form);
    }

    @GetMapping("/knots/{id}")
    public WebResult<KnotVO> getKnotById(@PathVariable Long id) {
        KnotDTO knotDTO = knotService.findKnotById(id);
        Assert.notNull(knotDTO, "绳结未找到");
        knotService.incrementViewCount(id);
        KnotVO vo = new KnotVO();
        BeanUtils.copyProperties(knotDTO, vo);
        vo.setCoverImage(FileService.buildImageUrl(knotDTO.getCoverImage()));
        vo.setImages(knotDTO.getImages().stream().map(e -> {
            KnotImageVO t = new KnotImageVO();
            BeanUtils.copyProperties(e, t);
            t.setImageUrl(FileService.buildImageUrl(t.getImageUrl()));
            return t;
        }).collect(Collectors.toList()));
        return WebResult.successResult(vo);

    }

    @GetMapping("/knots/popular")
    public WebQueryResult<KnotVO> getPopularKnots() {
        List<KnotVO> knots = knotService.getTopViewedKnots(POPULAR_LIMIT);
        return WebQueryResult.successResult(knots);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("绳结教程API服务运行正常！");
    }
}
