package com.yqmetals.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.AuditLog;

public interface AuditLogService extends IService<AuditLog> {
    void record(String username, String action, String target, String detail,
                String ip, String userAgent, boolean success);
    IPage<AuditLog> queryPage(int page, int size, String username, String action);
}
