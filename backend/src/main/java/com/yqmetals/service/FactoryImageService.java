package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.FactoryImage;

import java.util.List;

public interface FactoryImageService extends IService<FactoryImage> {
    List<FactoryImage> findAll();
}
