package com.knots.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knots.dto.KnotCategoryDTO;
import com.knots.entity.KnotCategory;
import com.knots.mapper.KnotCategoryMapper;
import com.knots.repository.KnotCategoryRepository;
import com.oak.root.web.form.BasePageableForm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService extends ServiceImpl<KnotCategoryMapper, KnotCategory> {

    @Autowired
    private KnotCategoryRepository categoryRepository;
    @Autowired
    private KnotCategoryMapper knotCategoryMapper;

    public PageInfo<KnotCategoryDTO> getCategoriesPage(BasePageableForm form) {
        PageHelper.startPage(form.getPage(), form.getPageSize());
        return new PageInfo<>(lambdaQuery().list().stream().map(e -> {
            KnotCategoryDTO t = new KnotCategoryDTO();
            BeanUtils.copyProperties(e, t);
            return t;
        }).collect(Collectors.toList()));
    }

    public List<KnotCategoryDTO> getAllCategories() {
        // 带 knotCount 的分类列表
        return knotCategoryMapper.selectAllWithKnotCount();
    }

}
