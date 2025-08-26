package com.knots.controller;

import com.github.pagehelper.PageInfo;
import com.knots.dto.KnotCategoryDTO;
import com.knots.dto.KnotDTO;
import com.knots.dto.UserDTO;
import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.entity.KnotImage;
import com.knots.entity.User;
import com.knots.service.CategoryService;
import com.knots.service.FileService;
import com.knots.service.KnotService;
import com.knots.service.UserService;
import com.knots.web.form.KnotQueryForm;
import com.knots.web.form.UserQueryForm;
import com.oak.root.web.form.BasePageableForm;
import com.oak.root.web.result.WebPageableResult;
import com.oak.root.web.result.WebResult;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    @Autowired
    private KnotService knotService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 绳结相关API
    @GetMapping("/knots")
    public WebPageableResult<KnotDTO> getKnots(KnotQueryForm queryForm) {
        PageInfo<KnotDTO> pageInfo = knotService.findKnotsByQuery(queryForm);
        return WebPageableResult.successResult(queryForm.getPage(), pageInfo.getTotal(), pageInfo.getList());
    }

    @PostMapping("/knots")
    public WebResult createKnot(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "1") int difficultyLevel,
            @RequestParam(defaultValue = "false") boolean isPublished,
            @RequestParam MultipartFile coverImage) {
        try {
            Knot knot = new Knot();
            knot.setName(name);
            knot.setDescription(description);
            knot.setDifficultyLevel(difficultyLevel);
            knot.setIsPublished(isPublished);
            knot.setViewCount(0);
            // 设置分类
            KnotCategory category = knotService.getCategoryById(categoryId);
            Assert.notNull(category, "分类不存在");
            knot.setCategoryId(categoryId);
            // 处理封面图片
            Assert.isTrue(coverImage != null && !coverImage.isEmpty(), "封面图片不能为空");
            String imagePath = fileService.uploadFile(coverImage);
            knot.setCoverImage(imagePath);
            Knot savedKnot = knotService.createKnot(knot);
            return WebResult.successResult(savedKnot);
        } catch (Exception e) {
            return WebResult.failResult("400", "创建失败：" + e.getMessage());
        }
    }

    @PutMapping("/knots/{id}")
    public WebResult updateKnot(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "1") int difficultyLevel,
            @RequestParam(defaultValue = "false") boolean isPublished,
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false) String[] imageRemarks,
            @RequestParam(required = false) Integer[] imageSortOrders) {

        try {
            Knot existingKnot = knotService.getKnotById(id);
            if (existingKnot == null) {
                return WebResult.failResult("-1", "绳结不存在");
            }

            existingKnot.setName(name);
            existingKnot.setDescription(description);
            existingKnot.setDifficultyLevel(difficultyLevel);
            existingKnot.setIsPublished(isPublished);

            // 设置分类
            KnotCategory category = knotService.getCategoryById(categoryId);
            Assert.notNull(category, "分类不存在");
            existingKnot.setCategoryId(categoryId);

            // 处理封面图片
            if (coverImage != null && !coverImage.isEmpty()) {
                // 删除旧图片
                if (existingKnot.getCoverImage() != null) {
                    fileService.deleteFile(existingKnot.getCoverImage());
                }
                String imagePath = fileService.uploadFile(coverImage);
                existingKnot.setCoverImage(imagePath);
            }

            // 处理多张图片
            if (images != null && images.length > 0) {
                for (int i = 0; i < images.length; i++) {
                    if (images[i] != null && !images[i].isEmpty()) {
                        String imagePath = fileService.uploadFile(images[i]);

                        // 创建KnotImage对象
                        KnotImage knotImage = new KnotImage();
                        knotImage.setImageUrl(imagePath);
                        knotImage.setImageName(images[i].getOriginalFilename());
                        knotImage.setKnotId(existingKnot.getId());

                        // 设置图片描述和排序
                        if (imageRemarks != null && i < imageRemarks.length) {
                            knotImage.setImageRemark(imageRemarks[i]);
                        }
                        if (imageSortOrders != null && i < imageSortOrders.length) {
                            knotImage.setSortOrder(imageSortOrders[i]);
                        } else {
                            knotImage.setSortOrder(i + 1);
                        }

                        // 保存图片
                        knotService.saveKnotImage(knotImage);
                    }
                }
            }

            knotService.updateKnot(existingKnot);
            return WebResult.successResult();

        } catch (Exception e) {
            return WebResult.failResult("400", "更新失败：" + e.getMessage());
        }
    }

    @GetMapping("/knots/{id}")
    public ResponseEntity<?> getKnot(@PathVariable Long id) {
        try {
            KnotDTO knot = knotService.findKnotById(id);
            if (knot == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("绳结不存在"));
            }
            return ResponseEntity.ok(createSuccessResponse("获取成功", knot));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("获取失败：" + e.getMessage()));
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
            // 删除所有绳结图片
            knotService.deleteKnotImages(id);
            knotService.deleteKnot(id);
            return ResponseEntity.ok(createSuccessResponse("绳结删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("删除失败：" + e.getMessage()));
        }
    }

    // 绳结图片相关API
    @PutMapping("/knot-images/{id}")
    public ResponseEntity<?> updateKnotImage(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            KnotImage image = knotService.getKnotImageById(id);
            if (image == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("图片不存在"));
            }
            if (request.containsKey("imageRemark")) {
                image.setImageRemark((String) request.get("imageRemark"));
            }
            if (request.containsKey("sortOrder")) {
                image.setSortOrder((Integer) request.get("sortOrder"));
            }
            KnotImage updatedImage = knotService.saveKnotImage(image);
            return ResponseEntity.ok(createSuccessResponse("图片更新成功", updatedImage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("更新失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/knot-images/{id}")
    public ResponseEntity<?> deleteKnotImage(@PathVariable Long id) {
        try {
            KnotImage image = knotService.getKnotImageById(id);
            if (image == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("图片不存在"));
            }
            // 删除文件
            if (image.getImageUrl() != null) {
                fileService.deleteFile(image.getImageUrl());
            }
            knotService.deleteKnotImage(id);
            return ResponseEntity.ok(createSuccessResponse("图片删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("删除失败：" + e.getMessage()));
        }
    }

    // 分类相关API
    @GetMapping("/categories")
    public WebPageableResult<KnotCategoryDTO> getCategories(BasePageableForm form) {
        PageInfo<KnotCategoryDTO> categoriesPage = categoryService.getCategoriesPage(form);
        return WebPageableResult.successResult(form.getPage(), categoriesPage.getTotal(), categoriesPage.getList());
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
    public WebPageableResult<UserDTO> getUsers(UserQueryForm form) {
        PageInfo<UserDTO> usersPage = userService.getUsersPage(form);
        return WebPageableResult.successResult(form.getPage(), usersPage.getTotal(), usersPage.getList());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
        }
        return ResponseEntity.ok(createSuccessResponse("获取成功", sanitizeUser(userOpt.get())));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request) {
        try {
            String username = (String) request.get("username");
            String password = (String) request.get("password");
            String nickName = (String) request.getOrDefault("nickName", null);
            String roleStr = (String) request.getOrDefault("role", "USER");

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名不能为空"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("密码不能为空"));
            }
            if (userService.existsByUsername(username)) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名已存在"));
            }

            User user = new User();
            user.setUsername(username.trim());
            user.setPassword(passwordEncoder.encode(password));
            user.setNickName(nickName);
            try {
                user.setRole(User.UserRole.valueOf(roleStr.toUpperCase()));
            } catch (Exception ex) {
                user.setRole(User.UserRole.USER);
            }

            User saved = userService.saveUser(user);
            return ResponseEntity.ok(createSuccessResponse("用户创建成功", sanitizeUser(saved)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("创建失败：" + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
            }

            User user = userOpt.get();

            if (request.containsKey("username")) {
                String newUsername = ((String) request.get("username")).trim();
                if (newUsername.isEmpty()) {
                    return ResponseEntity.badRequest().body(createErrorResponse("用户名不能为空"));
                }
                if (!newUsername.equals(user.getUsername()) && userService.existsByUsername(newUsername)) {
                    return ResponseEntity.badRequest().body(createErrorResponse("用户名已存在"));
                }
                user.setUsername(newUsername);
            }

            if (request.containsKey("nickName")) {
                user.setNickName((String) request.get("nickName"));
            }

            if (request.containsKey("role")) {
                String roleStr = (String) request.get("role");
                try {
                    user.setRole(User.UserRole.valueOf(roleStr.toUpperCase()));
                } catch (Exception ignored) {
                }
            }

            if (request.containsKey("password")) {
                String newPassword = (String) request.get("password");
                if (newPassword != null && !newPassword.trim().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                }
            }

            User saved = userService.updateUser(user);
            return ResponseEntity.ok(createSuccessResponse("用户更新成功", sanitizeUser(saved)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("更新失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户不存在"));
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(createSuccessResponse("用户删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("删除失败：" + e.getMessage()));
        }
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

    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickName", user.getNickName());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("role", user.getRole() != null ? user.getRole().name() : null);
        data.put("createdAt", user.getCreatedAt());
        data.put("updatedAt", user.getUpdatedAt());
        data.put("openId", user.getOpenId());
        return data;
    }
}
