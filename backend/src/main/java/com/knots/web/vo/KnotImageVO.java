package com.knots.web.vo;

import com.oak.root.base.bean.BaseInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnotImageVO extends BaseInfo {
    private static final long serialVersionUID = -6928076251240833199L;
    private String imageUrl;
    private String imageName;
    private String imageRemark;
    private Integer sortOrder;
}
