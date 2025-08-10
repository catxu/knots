package com.knots.controller;

import com.knots.entity.KnotCategory;
import com.knots.repository.KnotCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {
    
    @Autowired
    private KnotCategoryRepository categoryRepository;
    
    @GetMapping("/categories")
    public ResponseEntity<List<KnotCategory>> getCategories() {
        List<KnotCategory> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("绳结教程API服务运行正常！");
    }
}
