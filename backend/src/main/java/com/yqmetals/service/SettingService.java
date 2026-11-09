package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.Setting;

import java.util.Map;

public interface SettingService extends IService<Setting> {
    Map<String, String> getAll();
    String get(String key, String defaultValue);
    void saveAll(Map<String, String> settings);
    void save(String key, String value);
}
