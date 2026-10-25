package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yqmetals.common.enums.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private UserRole role;
    private Boolean active;
    @TableField(fill = FieldTime.ADD)
    private LocalDateTime createTime;
    @TableField(fill = FieldTime.UPDATE)
    private LocalDateTime updateTime;
    private Boolean deleted;
}
