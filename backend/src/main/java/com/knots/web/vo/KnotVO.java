package com.knots.web.vo;

import com.oak.root.base.bean.BaseInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KnotVO extends BaseInfo {
    private static final long serialVersionUID = -4914863228992484818L;
    private Long id;
    private String name;
    private Integer difficultyLevel;
    private String coverImage;
    private Integer viewCount;
    private List<KnotImageVO> images;
}
