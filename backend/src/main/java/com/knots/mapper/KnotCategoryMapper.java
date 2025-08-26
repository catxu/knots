package com.knots.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knots.dto.KnotCategoryDTO;
import com.knots.entity.KnotCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnotCategoryMapper extends BaseMapper<KnotCategory> {

    @Select("SELECT kc.*, ( SELECT COUNT(1) FROM knots k WHERE k.category_id = kc.id AND k.is_published = TRUE	) AS knot_count FROM knot_categories kc ORDER BY kc.sort_order ASC")
    List<KnotCategoryDTO> selectAllWithKnotCount();
}


