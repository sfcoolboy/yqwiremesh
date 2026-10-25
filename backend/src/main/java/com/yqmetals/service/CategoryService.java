package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> findTree();
    List<Category> findByParentId(Long parentId);
}
