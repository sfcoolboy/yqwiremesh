package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.entity.Product;
import com.yqmetals.mapper.ProductMapper;
import com.yqmetals.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return list(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getDeleted, false)
                .orderByAsc(Product::getSort));
    }

    @Override
    public List<Product> findAllActive() {
        return list(new LambdaQueryWrapper<Product>()
                .eq(Product::getDeleted, false)
                .orderByAsc(Product::getSort));
    }
}
