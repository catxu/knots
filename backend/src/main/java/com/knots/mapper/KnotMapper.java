package com.knots.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knots.dto.KnotDTO;
import com.knots.entity.Knot;
import com.knots.web.form.KnotQueryForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface KnotMapper extends BaseMapper<Knot> {
    List<KnotDTO> selectKnotWithCategory(@Param("query") KnotQueryForm form);
}


