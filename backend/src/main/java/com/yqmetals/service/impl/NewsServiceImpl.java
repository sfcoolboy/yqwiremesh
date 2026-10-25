package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.entity.News;
import com.yqmetals.mapper.NewsMapper;
import com.yqmetals.service.NewsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Override
    public List<News> findPublished() {
        return list(new LambdaQueryWrapper<News>()
                .eq(News::getDeleted, false)
                .orderByDesc(News::getPublishDate));
    }

    @Override
    public List<News> findByCategory(String category) {
        return list(new LambdaQueryWrapper<News>()
                .eq(News::getDeleted, false)
                .eq(News::getCategory, category)
                .orderByDesc(News::getPublishDate));
    }
}
