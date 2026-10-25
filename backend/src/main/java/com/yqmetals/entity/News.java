package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yqmetals.common.enums.NewsCategory;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("news")
public class News {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImageUrl;
    private NewsCategory category;
    private Integer viewCount;
    @TableField(fill = FieldTime.ADD)
    private LocalDateTime publishDate;
    @TableField(fill = FieldTime.ADD)
    private LocalDateTime createTime;
    @TableField(fill = FieldTime.UPDATE)
    private LocalDateTime updateTime;
    private Boolean deleted;
}
