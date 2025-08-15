package com.knots.dto;

import com.knots.entity.KnotImage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnotImageDTO {
    private Long id;
    private String imageUrl;
    private String imageName;
    private String imageRemark;
    private String imageType;
    private Long fileSize;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static KnotImageDTO fromEntity(KnotImage image) {
        KnotImageDTO dto = new KnotImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageName(image.getImageName());
        dto.setImageRemark(image.getImageRemark());
        dto.setImageType(image.getImageType());
        dto.setFileSize(image.getFileSize());
        dto.setSortOrder(image.getSortOrder());
        dto.setCreatedAt(image.getCreatedAt());
        dto.setUpdatedAt(image.getUpdatedAt());
        return dto;
    }
}
