package com.knots.controller;

import com.knots.dto.KnotCategoryDTO;
import com.knots.entity.User;
import com.knots.service.KnotService;
import com.knots.web.vo.KnotVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private KnotService knotService;

    // 登录页面
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    // 后台首页
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        // 检查登录状态
        User adminUser = (User) session.getAttribute("adminUser");
        if (adminUser == null) {
            return "redirect:/admin/login";
        }

        List<KnotVO> knots = knotService.getTopViewedKnots(5);
        model.addAttribute("knots", knots);
        model.addAttribute("adminUser", adminUser);
        return "admin/dashboard";
    }

    // 绳结管理页面
    @GetMapping("/knots")
    public String knotsList(Model model, HttpSession session) {
        // 检查登录状态
        User adminUser = (User) session.getAttribute("adminUser");
        if (adminUser == null) {
            return "redirect:/admin/login";
        }

        List<KnotCategoryDTO> categories = knotService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("adminUser", adminUser);
        return "admin/knots";
    }

    // 分类管理页面
    @GetMapping("/categories")
    public String categoriesList(Model model, HttpSession session) {
        // 检查登录状态
        User adminUser = (User) session.getAttribute("adminUser");
        if (adminUser == null) {
            return "redirect:/admin/login";
        }

        model.addAttribute("adminUser", adminUser);
        return "admin/categories";
    }

    // 用户管理页面
    @GetMapping("/users")
    public String usersList(Model model, HttpSession session) {
        // 检查登录状态
        User adminUser = (User) session.getAttribute("adminUser");
        if (adminUser == null) {
            return "redirect:/admin/login";
        }

        // 这里可以添加获取用户列表的逻辑
        // 暂时传递空列表，前端通过API获取
        model.addAttribute("adminUser", adminUser);
        return "admin/users";
    }
}
