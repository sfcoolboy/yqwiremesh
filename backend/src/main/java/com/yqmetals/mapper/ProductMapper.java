package com.yqmetals.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yqmetals.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
