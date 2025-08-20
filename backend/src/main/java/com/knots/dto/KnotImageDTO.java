package com.knots.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnotImageDTO {
    private Long id;
    private String imageUrl;
    private String imageName;
    private String imageRemark;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
