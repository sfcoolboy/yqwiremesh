package com.yqmetals.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_setting")
public class Setting {
    @TableId(type = IdType.INPUT)
    private String settingKey;
    private String settingValue;
}
