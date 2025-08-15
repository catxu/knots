package com.knots.service;

import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.entity.User;
import com.knots.repository.KnotRepository;
import com.knots.repository.KnotCategoryRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class KnotService {
    
    @Autowired
    private KnotRepository knotRepository;
    
    @Autowired
    private KnotCategoryRepository categoryRepository;
    
    public Page<Knot> getAllPublishedKnots(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return knotRepository.findByIsPublishedTrue(pageable);
    }
    
    public Page<Knot> searchKnots(String keyword, Pageable pageable) {
        return knotRepository.searchByKeyword(keyword, pageable);
    }
    
    public List<Knot> getKnotsByCategory(Long categoryId) {
        return knotRepository.findByCategoryId(categoryId);
    }
    
    public Page<Knot> getKnotsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        KnotCategory category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            return knotRepository.findByCategoryAndIsPublishedTrue(category, pageable);
        }
        return Page.empty(pageable);
    }
    
    public List<Knot> getTopViewedKnots(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return knotRepository.findTopViewedKnots(pageable);
    }
    
    public Knot getKnotById(Long id) {
        return knotRepository.findById(id).orElse(null);
    }
    
    public Knot createKnot(Knot knot, User createdBy) {
        knot.setCreatedBy(createdBy);
        return knotRepository.save(knot);
    }
    
    public Knot updateKnot(Knot knot) {
        return knotRepository.save(knot);
    }
    
    public void deleteKnot(Long id) {
        knotRepository.deleteById(id);
    }
    
    public void incrementViewCount(Long knotId) {
        Knot knot = knotRepository.findById(knotId).orElse(null);
        if (knot != null) {
            knot.setViewCount(knot.getViewCount() + 1);
            knotRepository.save(knot);
        }
    }
    
    public List<KnotCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }
    
    public KnotCategory getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }
    
    public KnotCategory createCategory(KnotCategory category) {
        return categoryRepository.save(category);
    }
    
    public KnotCategory updateCategory(KnotCategory category) {
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public Knot createKnot(Knot knot) {
        return knotRepository.save(knot);
    }
    
    // 新增分页方法
    public Page<Knot> getAllKnots(Pageable pageable) {
        return knotRepository.findAll(pageable);
    }
    
    public Page<Knot> getKnotsByStatus(boolean isPublished, Pageable pageable) {
        if (isPublished) {
            return knotRepository.findByIsPublishedTrue(pageable);
        } else {
            return knotRepository.findByIsPublishedFalse(pageable);
        }
    }
    
    public Page<KnotCategory> getCategoriesPage(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
}
