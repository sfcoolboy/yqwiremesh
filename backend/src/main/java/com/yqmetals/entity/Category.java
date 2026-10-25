package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String nameEn;
    private Integer sort;
    private String description;
    private String imageUrl;
    @TableField(fill = FieldTime.ADD)
    private LocalDateTime createTime;
    @TableField(fill = FieldTime.UPDATE)
    private LocalDateTime updateTime;
    private Boolean deleted;
}
