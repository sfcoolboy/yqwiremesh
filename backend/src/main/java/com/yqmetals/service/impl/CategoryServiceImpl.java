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
        List<Category> roots = list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, null)
                .eq(Category::getDeleted, false)
                .orderByAsc(Category::getSort));
        roots.forEach(r -> r.setChildren(
                list(new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, r.getId())
                        .eq(Category::getDeleted, false)
                        .orderByAsc(Category::getSort))));
        return roots;
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getDeleted, false)
                .orderByAsc(Category::getSort));
    }
}
