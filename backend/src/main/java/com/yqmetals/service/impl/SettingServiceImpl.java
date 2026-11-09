package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.entity.Setting;
import com.yqmetals.mapper.SettingMapper;
import com.yqmetals.service.SettingService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {

    @Override
    public void save(String key, String value) {
        Setting setting = new Setting();
        setting.setSettingKey(key);
        setting.setSettingValue(value == null ? "" : value.trim());
        saveOrUpdate(setting);
    }

    @Override
    public Map<String, String> getAll() {
        Map<String, String> result = new HashMap<>();
        list().forEach(s -> result.put(s.getSettingKey(), s.getSettingValue()));
        return result;
    }

    @Override
    public String get(String key, String defaultValue) {
        Setting setting = getById(key);
        return setting != null && setting.getSettingValue() != null && !setting.getSettingValue().isBlank()
                ? setting.getSettingValue()
                : defaultValue;
    }

    @Override
    public void saveAll(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            save(key, value);
        });
    }
}
