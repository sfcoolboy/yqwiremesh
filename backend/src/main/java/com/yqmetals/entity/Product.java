package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private Integer sort;
    @TableField(fill = FieldTime.ADD)
    private LocalDateTime createTime;
    @TableField(fill = FieldTime.UPDATE)
    private LocalDateTime updateTime;
    private Boolean deleted;
}
