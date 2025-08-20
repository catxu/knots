package com.knots.dto;

import com.knots.entity.KnotImage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnotDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String steps;
    private String coverImage;
    private String categoryName;
    private Integer difficultyLevel;
    private Integer viewCount;
    private Boolean isPublished;
    private List<KnotImage> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
