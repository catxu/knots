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
import com.knots.web.form.CategoryCreateForm;
import com.knots.web.form.KnotQueryForm;
import com.knots.web.form.UserQueryForm;
import com.oak.root.web.form.BasePageableForm;
import com.oak.root.web.result.WebPageableResult;
import com.oak.root.web.result.WebResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        pageInfo.getList().forEach(e -> e.setCoverImage(FileService.buildImageUrl(e.getCoverImage())));
        return WebPageableResult.successResult(queryForm.getPage(), pageInfo.getTotal(), pageInfo.getList());
    }

    @PostMapping("/knots")
    public WebResult createKnot(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String steps,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "1") int difficultyLevel,
            @RequestParam(defaultValue = "false") boolean isPublished,
            @RequestParam MultipartFile coverImage) {
        try {
            Knot knot = new Knot();
            knot.setName(name);
            knot.setDescription(description);
            knot.setSteps(steps);
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
            return WebResult.emptyResult();
        } catch (Exception e) {
            return WebResult.failResult("400", "创建失败：" + e.getMessage());
        }
    }

    @PutMapping("/knots/{id}")
    public WebResult updateKnot(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String steps,
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
            existingKnot.setDescription(steps);
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
    public WebResult<KnotDTO> getKnot(@PathVariable Long id) {
        KnotDTO knotDTO = knotService.findKnotById(id);
        Assert.notNull(knotDTO, "绳结不存在");
        knotDTO.setCoverImage(FileService.buildImageUrl(knotDTO.getCoverImage()));
        knotDTO.getImages().forEach(e -> e.setImageUrl(FileService.buildImageUrl(e.getImageUrl())));
        return WebResult.successResult(knotDTO);
    }

    @DeleteMapping("/knots/{id}")
    public WebResult deleteKnot(@PathVariable Long id) {
        Knot knot = knotService.getKnotById(id);
        Assert.notNull(knot, "绳结不存在");
        // 删除封面图片
        if (knot.getCoverImage() != null) {
            fileService.deleteFile(knot.getCoverImage());
        }
        // 删除所有绳结图片
        knotService.deleteKnotImages(id);
        knotService.deleteKnot(id);
        return WebResult.emptyResult();
    }

    // 绳结图片相关API
    @PutMapping("/knot-images/{id}")
    public WebResult updateKnotImage(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        KnotImage image = knotService.getKnotImageById(id);
        Assert.notNull(image, "图片不存在");
        if (request.containsKey("imageRemark")) {
            image.setImageRemark((String) request.get("imageRemark"));
        }
        if (request.containsKey("sortOrder")) {
            image.setSortOrder((Integer) request.get("sortOrder"));
        }
        KnotImage updatedImage = knotService.saveKnotImage(image);
        return WebResult.emptyResult();
    }

    @DeleteMapping("/knot-images/{id}")
    public WebResult deleteKnotImage(@PathVariable Long id) {
        KnotImage image = knotService.getKnotImageById(id);
        Assert.notNull(image, "图片不存在");
        // 删除文件
        if (image.getImageUrl() != null) {
            fileService.deleteFile(image.getImageUrl());
        }
        knotService.deleteKnotImage(id);
        return WebResult.emptyResult();
    }

    // 分类相关API
    @GetMapping("/categories")
    public WebPageableResult<KnotCategoryDTO> getCategories(BasePageableForm form) {
        PageInfo<KnotCategoryDTO> categoriesPage = categoryService.getCategoriesPage(form);
        return WebPageableResult.successResult(form.getPage(), categoriesPage.getTotal(), categoriesPage.getList());
    }

    @PostMapping("/categories")
    public WebResult createCategory(@RequestBody CategoryCreateForm form) {
        KnotCategory category = new KnotCategory();
        category.setName(form.getName());
        category.setDescription(form.getDescription());
        category.setSortOrder(form.getSortOrder());
        knotService.createCategory(category);
        return WebResult.successResult();
    }

    @PutMapping("/categories/{id}")
    public WebResult updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int sortOrder) {
        KnotCategory category = knotService.getCategoryById(id);
        Assert.notNull(category, "分类不存在");
        category.setName(name);
        category.setDescription(description);
        category.setSortOrder(sortOrder);
        KnotCategory updatedCategory = knotService.updateCategory(category);
        return WebResult.emptyResult();
    }

    @DeleteMapping("/categories/{id}")
    public WebResult deleteCategory(@PathVariable Long id) {
        KnotCategory category = knotService.getCategoryById(id);
        Assert.notNull(category, "分类不存在");
        knotService.deleteCategory(id);
        return WebResult.emptyResult();
    }

    @GetMapping("/categories/{id}")
    public WebResult<KnotCategoryDTO> getCategory(@PathVariable Long id) {
        KnotCategory category = knotService.getCategoryById(id);
        Assert.notNull(category, "分类不存在");
        KnotCategoryDTO t = new KnotCategoryDTO();
        BeanUtils.copyProperties(category, t);
        return WebResult.successResult(t);
    }

    // 用户相关API
    @GetMapping("/users")
    public WebPageableResult<UserDTO> getUsers(UserQueryForm form) {
        PageInfo<UserDTO> usersPage = userService.getUsersPage(form);
        return WebPageableResult.successResult(form.getPage(), usersPage.getTotal(), usersPage.getList());
    }

    @GetMapping("/users/{id}")
    public WebResult<UserDTO> getUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        Assert.isTrue(userOpt.isPresent(), "用户不存在");
        UserDTO t = new UserDTO();
        BeanUtils.copyProperties(userOpt.get(), t);
        return WebResult.successResult(t);
    }

    @PostMapping("/users")
    public WebResult createUser(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        String nickName = (String) request.getOrDefault("nickName", null);
        String roleStr = (String) request.getOrDefault("role", "USER");

        Assert.hasText(username, "用户名不能为空");
        Assert.hasText(password, "密码不能为空");
        Assert.isTrue(!userService.existsByUsername(username), "用户名已存在");

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
        return WebResult.emptyResult();
    }

    @PutMapping("/users/{id}")
    public WebResult updateUser(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Optional<User> userOpt = userService.findById(id);
        Assert.isTrue(userOpt.isPresent(), "用户不存在");

        User user = userOpt.get();

        if (request.containsKey("username")) {
            String newUsername = ((String) request.get("username")).trim();
            Assert.hasText(newUsername, "用户名不能为空");
            Assert.isTrue(!newUsername.equals(user.getUsername()) && !userService.existsByUsername(newUsername), "用户名已存在");
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
        return WebResult.emptyResult();
    }

    @DeleteMapping("/users/{id}")
    public WebResult deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        Assert.isTrue(userOpt.isPresent(), "用户不存在");
        userService.deleteUser(id);
        return WebResult.emptyResult();
    }
}
