package com.knots.controller;

import com.knots.entity.User;
import com.knots.service.UserService;
import com.knots.web.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // 登录API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm loginRequest, HttpSession session) {
        try {
            // 验证用户名和密码
            Optional<User> userOpt = userService.findByUsername(loginRequest.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名或密码错误"));
            }

            User user = userOpt.get();

            // 检查用户角色
            if (user.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.badRequest().body(createErrorResponse("权限不足，只有管理员可以登录"));
            }

            // 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(createErrorResponse("用户名或密码错误"));
            }

            // 创建认证token
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), loginRequest.getPassword());

            // 进行认证
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 将用户信息存储到session
            session.setAttribute("adminUser", user);
            session.setAttribute("adminUsername", user.getUsername());

            // 返回成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole().name()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse("登录失败：" + e.getMessage()));
        }
    }

    // 登出API
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");

        return ResponseEntity.ok(response);
    }

    // 检查登录状态API
    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(HttpSession session) {
        String adminUsername = (String) session.getAttribute("adminUsername");

        if (adminUsername != null) {
            Optional<User> userOpt = userService.findByUsername(adminUsername);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("loggedIn", true);
                response.put("user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                ));
                return ResponseEntity.ok(response);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("loggedIn", false);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
