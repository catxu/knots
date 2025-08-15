package com.knots.controller;

import com.knots.dto.PageResponse;
import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.entity.User;
import com.knots.service.FileService;
import com.knots.service.KnotService;
import com.knots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {
    
    @Autowired
    private KnotService knotService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileService fileService;
    
    // 绳结相关API
    @GetMapping("/knots")
    public ResponseEntity<?> getKnots(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isPublished) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Knot> knotsPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            knotsPage = knotService.searchKnots(keyword, pageable);
        } else if (categoryId != null) {
            knotsPage = knotService.getKnotsByCategory(categoryId, page, size);
        } else if (isPublished != null) {
            knotsPage = knotService.getKnotsByStatus(isPublished, pageable);
        } else {
            knotsPage = knotService.getAllKnots(pageable);
        }
        
        return ResponseEntity.ok(PageResponse.of(knotsPage));
    }
    
    @PostMapping("/knots")
    public ResponseEntity<?> createKnot(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "1") int difficultyLevel,
            @RequestParam(defaultValue = "false") boolean isPublished,
            @RequestParam(required = false) MultipartFile coverImage) {
        
        try {
            Knot knot = new Knot();
            knot.setName(name);
            knot.setDescription(description);
            knot.setDifficultyLevel(difficultyLevel);
            knot.setIsPublished(isPublished);
            knot.setViewCount(0);
            
            // 设置分类
            KnotCategory category = knotService.getCategoryById(categoryId);
            if (category == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("分类不存在"));
            }
            knot.setCategory(category);
            
            // 处理封面图片
            if (coverImage != null && !coverImage.isEmpty()) {
                String imagePath = fileService.uploadFile(coverImage);
                knot.setCoverImage(imagePath);
            }
            
            Knot savedKnot = knotService.createKnot(knot);
            return ResponseEntity.ok(createSuccessResponse("绳结创建成功", savedKnot));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("创建失败：" + e.getMessage()));
        }
    }
    
    @PutMapping("/knots/{id}")
    public ResponseEntity<?> updateKnot(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "1") int difficultyLevel,
            @RequestParam(defaultValue = "false") boolean isPublished,
            @RequestParam(required = false) MultipartFile coverImage) {
        
        try {
            Knot existingKnot = knotService.getKnotById(id);
            if (existingKnot == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("绳结不存在"));
            }
            
            existingKnot.setName(name);
            existingKnot.setDescription(description);
            existingKnot.setDifficultyLevel(difficultyLevel);
            existingKnot.setIsPublished(isPublished);
            
            // 设置分类
            KnotCategory category = knotService.getCategoryById(categoryId);
            if (category == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("分类不存在"));
            }
            existingKnot.setCategory(category);
            
            // 处理封面图片
            if (coverImage != null && !coverImage.isEmpty()) {
                // 删除旧图片
                if (existingKnot.getCoverImage() != null) {
                    fileService.deleteFile(existingKnot.getCoverImage());
                }
                String imagePath = fileService.uploadFile(coverImage);
                existingKnot.setCoverImage(imagePath);
            }
            
            Knot updatedKnot = knotService.updateKnot(existingKnot);
            return ResponseEntity.ok(createSuccessResponse("绳结更新成功", updatedKnot));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("更新失败：" + e.getMessage()));
        }
    }
    
    @DeleteMapping("/knots/{id}")
    public ResponseEntity<?> deleteKnot(@PathVariable Long id) {
        try {
            Knot knot = knotService.getKnotById(id);
            if (knot == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("绳结不存在"));
            }
            
            // 删除封面图片
            if (knot.getCoverImage() != null) {
                fileService.deleteFile(knot.getCoverImage());
            }
            
            knotService.deleteKnot(id);
            return ResponseEntity.ok(createSuccessResponse("绳结删除成功", null));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("删除失败：" + e.getMessage()));
        }
    }
    
    // 分类相关API
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());
        Page<KnotCategory> categoriesPage = knotService.getCategoriesPage(pageable);
        
        return ResponseEntity.ok(PageResponse.of(categoriesPage));
    }
    
    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int sortOrder) {
        
        try {
            KnotCategory category = new KnotCategory();
            category.setName(name);
            category.setDescription(description);
            category.setSortOrder(sortOrder);
            
            KnotCategory savedCategory = knotService.createCategory(category);
            return ResponseEntity.ok(createSuccessResponse("分类创建成功", savedCategory));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("创建失败：" + e.getMessage()));
        }
    }
    
    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int sortOrder) {
        
        try {
            KnotCategory category = knotService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("分类不存在"));
            }
            
            category.setName(name);
            category.setDescription(description);
            category.setSortOrder(sortOrder);
            
            KnotCategory updatedCategory = knotService.updateCategory(category);
            return ResponseEntity.ok(createSuccessResponse("分类更新成功", updatedCategory));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("更新失败：" + e.getMessage()));
        }
    }
    
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            KnotCategory category = knotService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("分类不存在"));
            }
            
            knotService.deleteCategory(id);
            return ResponseEntity.ok(createSuccessResponse("分类删除成功", null));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("删除失败：" + e.getMessage()));
        }
    }
    
    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        try {
            KnotCategory category = knotService.getCategoryById(id);
            if (category == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("分类不存在"));
            }
            
            return ResponseEntity.ok(createSuccessResponse("获取成功", category));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("获取失败：" + e.getMessage()));
        }
    }
    
    // 用户相关API
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> usersPage = userService.getUsersPage(pageable);
        
        return ResponseEntity.ok(PageResponse.of(usersPage));
    }
    
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
