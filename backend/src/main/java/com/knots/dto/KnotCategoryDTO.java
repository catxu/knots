package com.knots.dto;

import com.oak.root.base.bean.BaseInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnotCategoryDTO extends BaseInfo {
    private static final long serialVersionUID = 7874170120118465178L;
    private Long id;
    private String name;
    private String description;
}
