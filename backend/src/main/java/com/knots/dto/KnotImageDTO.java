package com.knots.dto;

import com.oak.root.base.bean.BaseInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class KnotImageDTO extends BaseInfo {
    private static final long serialVersionUID = 7552215263624676764L;
    private Long id;
    private String imageUrl;
    private String imageName;
    private String imageRemark;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
