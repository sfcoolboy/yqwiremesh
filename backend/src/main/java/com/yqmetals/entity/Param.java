package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("param")
public class Param {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String unit;
    private Boolean enabled;
    private String assignedCategoryIds;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
