package com.knots.service;

import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.entity.User;
import com.knots.repository.KnotRepository;
import com.knots.repository.KnotCategoryRepository;
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
    
    public Page<Knot> searchKnots(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return knotRepository.searchByKeyword(keyword, pageable);
    }
    
    public List<Knot> getKnotsByCategory(Long categoryId) {
        return knotRepository.findByCategoryId(categoryId);
    }
    
    public List<Knot> getTopViewedKnots(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return knotRepository.findTopViewedKnots(pageable);
    }
    
    public Optional<Knot> getKnotById(Long id) {
        return knotRepository.findById(id);
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
        Optional<Knot> knotOpt = knotRepository.findById(knotId);
        if (knotOpt.isPresent()) {
            Knot knot = knotOpt.get();
            knot.setViewCount(knot.getViewCount() + 1);
            knotRepository.save(knot);
        }
    }
    
    public List<KnotCategory> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }
    
    public Optional<KnotCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
}
