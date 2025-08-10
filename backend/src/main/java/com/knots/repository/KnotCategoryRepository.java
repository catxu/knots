package com.knots.repository;

import com.knots.entity.KnotCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnotCategoryRepository extends JpaRepository<KnotCategory, Long> {
    List<KnotCategory> findAllByOrderBySortOrderAsc();
    boolean existsByName(String name);
}
