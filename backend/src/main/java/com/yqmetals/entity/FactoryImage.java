package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("factory_image")
public class FactoryImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String imageUrl;
    private String caption;
    private Integer sort;
    private String type;
    private String page;
    private Boolean showOnFactory;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    private Boolean deleted;
}

