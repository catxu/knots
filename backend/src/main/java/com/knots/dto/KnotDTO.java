package com.knots.dto;

import com.oak.root.base.bean.BaseInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class KnotDTO extends BaseInfo {
    private static final long serialVersionUID = -7517116935324397718L;
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
    private List<KnotImageDTO> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
