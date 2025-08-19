package com.knots.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knots.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}


