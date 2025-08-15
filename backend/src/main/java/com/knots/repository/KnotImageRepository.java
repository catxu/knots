package com.knots.repository;

import com.knots.entity.KnotImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnotImageRepository extends JpaRepository<KnotImage, Long> {
    List<KnotImage> findByKnotIdOrderBySortOrderAsc(Long knotId);
    void deleteByKnotId(Long knotId);
}
