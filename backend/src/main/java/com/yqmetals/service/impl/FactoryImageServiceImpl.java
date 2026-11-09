package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.entity.FactoryImage;
import com.yqmetals.mapper.FactoryImageMapper;
import com.yqmetals.service.FactoryImageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactoryImageServiceImpl extends ServiceImpl<FactoryImageMapper, FactoryImage> implements FactoryImageService {

    @Override
    public List<FactoryImage> findAll() {
        return list(new LambdaQueryWrapper<FactoryImage>()
                .eq(FactoryImage::getDeleted, false)
                .orderByAsc(FactoryImage::getSort)
                .orderByAsc(FactoryImage::getId));
    }
}
