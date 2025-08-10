package com.knots.controller;

import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.service.KnotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    public String dashboard(Model model) {
        List<Knot> knots = knotService.getTopViewedKnots(5);
        model.addAttribute("knots", knots);
        return "admin/dashboard";
    }
    
    // 绳结管理页面
    @GetMapping("/knots")
    public String knotsList(Model model) {
        List<Knot> knots = knotService.getAllPublishedKnots(0, 100).getContent();
        model.addAttribute("knots", knots);
        return "admin/knots";
    }
    
    // 分类管理页面
    @GetMapping("/categories")
    public String categoriesList(Model model) {
        List<KnotCategory> categories = knotService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }
}
