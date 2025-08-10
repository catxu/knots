package com.knots.controller;

import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.entity.User;
import com.knots.service.KnotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        
        List<Knot> knots = knotService.getTopViewedKnots(5);
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
        
        List<Knot> knots = knotService.getAllPublishedKnots(0, 100).getContent();
        List<KnotCategory> categories = knotService.getAllCategories();
        model.addAttribute("knots", knots);
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
        
        List<KnotCategory> categories = knotService.getAllCategories();
        
        model.addAttribute("categories", categories);
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("totalKnots", 0);
        model.addAttribute("publishedKnots", 0);
        model.addAttribute("totalViews", 0);
        model.addAttribute("adminUser", adminUser);
        return "admin/categories";
    }
}
