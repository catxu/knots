package com.knots.repository;

import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnotRepository extends JpaRepository<Knot, Long> {
    
    Page<Knot> findByIsPublishedTrue(Pageable pageable);
    
    Page<Knot> findByCategoryAndIsPublishedTrue(KnotCategory category, Pageable pageable);
    
    @Query("SELECT k FROM Knot k WHERE k.isPublished = true AND " +
           "(k.name LIKE %:keyword% OR k.description LIKE %:keyword%)")
    Page<Knot> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT k FROM Knot k WHERE k.isPublished = true AND k.category.id = :categoryId")
    List<Knot> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT k FROM Knot k WHERE k.isPublished = true ORDER BY k.viewCount DESC")
    List<Knot> findTopViewedKnots(Pageable pageable);
    
    Page<Knot> findByIsPublishedFalse(Pageable pageable);
}
