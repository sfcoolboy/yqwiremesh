package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.News;
import java.util.List;

public interface NewsService extends IService<News> {
    List<News> findPublished();
    List<News> findByCategory(String category);
}
