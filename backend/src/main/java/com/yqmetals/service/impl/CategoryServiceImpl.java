package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.entity.Category;
import com.yqmetals.mapper.CategoryMapper;
import com.yqmetals.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> findTree() {
        return list(new LambdaQueryWrapper<Category>()
                .isNull(Category::getParentId)
                .eq(Category::getType, "PRODUCT")
                .eq(Category::getDeleted, false)
                .orderByAsc(Category::getSort));
    }

    @Override
    public List<Category> findByType(String type) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, type)
                .eq(Category::getDeleted, false)
                .orderByAsc(Category::getSort));
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getDeleted, false)
                .orderByAsc(Category::getSort));
    }
}
