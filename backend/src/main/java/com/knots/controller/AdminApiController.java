package com.knots.controller;

import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.entity.User;
import com.knots.service.KnotService;
import com.knots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private KnotService knotService;

    @Autowired
    private UserService userService;

    // ==================== 分类管理API ====================

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<KnotCategory> categories = knotService.getAllCategories();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取分类列表失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Optional<KnotCategory> categoryOpt = knotService.getCategoryById(id);
            if (categoryOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", categoryOpt.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "分类不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取分类信息失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@RequestBody KnotCategory category) {
        try {
            KnotCategory savedCategory = knotService.createCategory(category);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedCategory);
            response.put("message", "分类创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建分类失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody KnotCategory category) {
        try {
            category.setId(id);
            KnotCategory updatedCategory = knotService.updateCategory(category);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedCategory);
            response.put("message", "分类更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新分类失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            knotService.deleteCategory(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分类删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除分类失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 绳结管理API ====================

    @GetMapping("/knots")
    public ResponseEntity<?> getAllKnots() {
        try {
            List<Knot> knots = knotService.getAllPublishedKnots(0, 1000).getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", knots);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取绳结列表失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/knots/{id}")
    public ResponseEntity<?> getKnotById(@PathVariable Long id) {
        try {
            Optional<Knot> knotOpt = knotService.getKnotById(id);
            if (knotOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", knotOpt.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "绳结不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取绳结信息失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/knots")
    public ResponseEntity<?> createKnot(@RequestParam("name") String name,
                                       @RequestParam("description") String description,
                                       @RequestParam("categoryId") Long categoryId,
                                       @RequestParam("difficultyLevel") Integer difficultyLevel,
                                       @RequestParam("isPublished") Boolean isPublished,
                                       @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        try {
            // 验证必填字段
            if (name == null || name.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "绳结名称不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (description == null || description.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "绳结描述不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (categoryId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "请选择分类");
                return ResponseEntity.badRequest().body(response);
            }

            // 创建绳结对象
            Knot knot = new Knot();
            knot.setName(name.trim());
            knot.setDescription(description.trim());
            
            // 设置分类
            Optional<KnotCategory> categoryOpt = knotService.getCategoryById(categoryId);
            if (categoryOpt.isPresent()) {
                knot.setCategory(categoryOpt.get());
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "指定的分类不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            knot.setDifficultyLevel(difficultyLevel != null ? difficultyLevel : 1);
            knot.setIsPublished(isPublished != null ? isPublished : false);
            knot.setViewCount(0);

            // 处理封面图片
            if (coverImage != null && !coverImage.isEmpty()) {
                // 这里可以添加图片上传逻辑
                // 暂时设置为默认图片路径
                knot.setCoverImage("/images/default-knot.jpg");
            }

            Knot savedKnot = knotService.createKnot(knot);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedKnot);
            response.put("message", "绳结创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建绳结失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/knots/{id}")
    public ResponseEntity<?> updateKnot(@PathVariable Long id, @RequestBody Knot knot) {
        try {
            knot.setId(id);
            Knot updatedKnot = knotService.updateKnot(knot);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedKnot);
            response.put("message", "绳结更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新绳结失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/knots/{id}")
    public ResponseEntity<?> deleteKnot(@PathVariable Long id) {
        try {
            knotService.deleteKnot(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "绳结删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除绳结失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== 用户管理API ====================

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取用户列表失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", userOpt.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取用户信息失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "用户更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "更新用户失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "删除用户失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // 验证必填字段
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 检查用户名是否已存在
            if (userService.existsByUsername(user.getUsername())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户名已存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            User savedUser = userService.createUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", savedUser);
            response.put("message", "用户创建成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "创建用户失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
