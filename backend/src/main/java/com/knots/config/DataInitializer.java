package com.knots.config;

import com.knots.entity.KnotCategory;
import com.knots.entity.User;
import com.knots.repository.KnotCategoryRepository;
import com.knots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KnotCategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 创建默认管理员用户
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);
        }

        // 创建默认分类
        if (categoryRepository.count() == 0) {
            String[] categories = {"基础结", "实用结", "装饰结", "专业结", "救援结"};
            for (int i = 0; i < categories.length; i++) {
                KnotCategory category = new KnotCategory();
                category.setName(categories[i]);
                category.setDescription(categories[i] + "分类描述");
                category.setSortOrder(i + 1);
                categoryRepository.save(category);
            }
        }
    }
}
