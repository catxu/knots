package com.knots.service;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knots.dto.KnotDTO;
import com.knots.dto.KnotImageDTO;
import com.knots.entity.Knot;
import com.knots.entity.KnotCategory;
import com.knots.entity.KnotImage;
import com.knots.mapper.KnotCategoryMapper;
import com.knots.mapper.KnotMapper;
import com.knots.repository.KnotCategoryRepository;
import com.knots.repository.KnotImageRepository;
import com.knots.repository.KnotRepository;
import com.knots.web.form.KnotQueryForm;
import com.knots.web.vo.KnotVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnotService extends ServiceImpl<KnotMapper, Knot> {

    @Autowired
    private KnotRepository knotRepository;

    @Autowired
    private KnotCategoryRepository categoryRepository;

    @Autowired
    private KnotCategoryMapper knotCategoryMapper;

    @Autowired
    private KnotMapper knotMapper;

    @Autowired
    private KnotImageRepository knotImageRepository;

    @Autowired
    private FileService fileService;

    public Page<Knot> getAllPublishedKnots(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return knotRepository.findByIsPublishedTrue(pageable);
    }

    public Page<Knot> searchKnots(String keyword, Pageable pageable) {
        return knotRepository.searchByKeyword(keyword, pageable);
    }

    public List<Knot> getKnotsByCategory(Long categoryId) {
        return knotRepository.findByCategoryId(categoryId);
    }

    public Page<Knot> getKnotsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        KnotCategory category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            return knotRepository.findByCategoryIdAndIsPublishedTrue(category, pageable);
        }
        return Page.empty(pageable);
    }

    public List<KnotVO> getTopViewedKnots(int limit) {
        PageHelper.startPage(1, limit);
        List<Knot> knots = lambdaQuery().orderByDesc(Knot::getViewCount).list();
        List<KnotVO> res = knots.stream().map(e -> {
            KnotVO info = new KnotVO();
            info.setId(e.getId());
            info.setName(e.getName());
            info.setDifficultyLevel(e.getDifficultyLevel());
            info.setCoverImage(FileService.buildImageUrl(e.getCoverImage()));
            info.setViewCount(e.getViewCount());
            return info;
        }).collect(Collectors.toList());
        return res;
    }

    public Knot getKnotById(Long id) {
        return knotRepository.findById(id).orElse(null);
    }

    public KnotDTO findKnotById(Long id) {
        PageHelper.startPage(1, 1);
        KnotQueryForm query = new KnotQueryForm();
        query.setKnotId(id);
        List<KnotDTO> knotDTOS = knotMapper.selectKnotWithCategory(query);
        Assert.notEmpty(knotDTOS, "绳结不存在");
        List<KnotImage> images = knotImageRepository.findByKnotIdOrderBySortOrderAsc(id);
        KnotDTO entity = knotDTOS.get(0);
        entity.setImages(images.stream().map(e -> {
            KnotImageDTO t = new KnotImageDTO();
            BeanUtils.copyProperties(e, t);
            return t;
        }).collect(Collectors.toList()));
        return entity;
    }

    public Knot updateKnot(Knot knot) {
        return knotRepository.save(knot);
    }

    public void deleteKnot(Long id) {
        knotRepository.deleteById(id);
    }

    public void incrementViewCount(Long knotId) {
        Knot knot = knotRepository.findById(knotId).orElse(null);
        if (knot != null) {
            knot.setViewCount(knot.getViewCount() + 1);
            knotRepository.save(knot);
        }
    }

    public KnotCategory getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public KnotCategory createCategory(KnotCategory category) {
        return categoryRepository.save(category);
    }

    public KnotCategory updateCategory(KnotCategory category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public Knot createKnot(Knot knot) {
        return knotRepository.save(knot);
    }

    // 条件查询方法
    public PageInfo<KnotDTO> findKnotsByQuery(KnotQueryForm queryForm) {
        PageHelper.startPage(queryForm.getPage(), queryForm.getPageSize());
        return new PageInfo<>(knotMapper.selectKnotWithCategory(queryForm));
    }

    // 保存绳结图片
    public KnotImage saveKnotImage(KnotImage knotImage) {
        return knotImageRepository.save(knotImage);
    }

    // 获取绳结的所有图片
    public List<KnotImage> getKnotImages(Long knotId) {
        return knotImageRepository.findByKnotIdOrderBySortOrderAsc(knotId);
    }

    // 删除绳结图片
    public void deleteKnotImage(Long imageId) {
        knotImageRepository.deleteById(imageId);
    }

    // 删除绳结的所有图片
    public void deleteKnotImages(Long knotId) {
        knotImageRepository.deleteByKnotId(knotId);
    }

    // 根据ID获取绳结图片
    public KnotImage getKnotImageById(Long id) {
        return knotImageRepository.findById(id).orElse(null);
    }

    public PageInfo<KnotDTO> searchKnots(KnotQueryForm form) {
        PageHelper.startPage(form.getPage(), form.getPageSize());
        return new PageInfo<>(knotMapper.selectKnotWithCategory(form));
    }
}
