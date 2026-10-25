package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yqmetals.common.enums.InquiryStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inquiry")
public class Inquiry {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String company;
    private String country;
    private String email;
    private String phone;
    private Long categoryId;
    private String message;
    private InquiryStatus status;
    private Long assignedTo;
    @TableField(fill = FieldTime.ADD)
    private LocalDateTime createTime;
    @TableField(fill = FieldTime.UPDATE)
    private LocalDateTime updateTime;
    private Boolean deleted;
}
