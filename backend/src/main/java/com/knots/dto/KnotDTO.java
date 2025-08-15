package com.knots.dto;

import com.knots.dto.KnotImageDTO;
import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class KnotDTO {
    private Long id;
    private String name;
    private String description;
    private String steps;
    private String coverImage;
    private CategoryDTO category;
    private Integer difficultyLevel;
    private Integer viewCount;
    private Boolean isPublished;
    private List<KnotImageDTO> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static KnotDTO fromEntity(Knot knot) {
        KnotDTO dto = new KnotDTO();
        dto.setId(knot.getId());
        dto.setName(knot.getName());
        dto.setDescription(knot.getDescription());
        dto.setSteps(knot.getSteps());
        dto.setCoverImage(knot.getCoverImage());
        dto.setDifficultyLevel(knot.getDifficultyLevel());
        dto.setViewCount(knot.getViewCount());
        dto.setIsPublished(knot.getIsPublished());
        dto.setCreatedAt(knot.getCreatedAt());
        dto.setUpdatedAt(knot.getUpdatedAt());
        
        // 处理分类
        if (knot.getCategory() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(knot.getCategory().getId());
            categoryDTO.setName(knot.getCategory().getName());
            categoryDTO.setDescription(knot.getCategory().getDescription());
            dto.setCategory(categoryDTO);
        }
        
        // 处理图片列表
        if (knot.getImages() != null) {
            dto.setImages(knot.getImages().stream()
                .map(KnotImageDTO::fromEntity)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    @Data
    public static class CategoryDTO {
        private Long id;
        private String name;
        private String description;
    }
}
