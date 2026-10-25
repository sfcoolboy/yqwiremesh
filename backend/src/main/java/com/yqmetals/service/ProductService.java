package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.Product;
import java.util.List;

public interface ProductService extends IService<Product> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findAllActive();
}
