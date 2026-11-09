package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.entity.AuditLog;
import com.yqmetals.mapper.AuditLogMapper;
import com.yqmetals.service.AuditLogService;
import org.springframework.stereotype.Service;

@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditLogService {

    @Override
    public void record(String username, String action, String target, String detail,
                       String ip, String userAgent, boolean success) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        log.setIp(ip);
        log.setUserAgent(userAgent == null ? null : truncate(userAgent, 500));
        log.setSuccess(success);
        save(log);
    }

    @Override
    public IPage<AuditLog> queryPage(int page, int size, String username, String action) {
        LambdaQueryWrapper<AuditLog> qw = new LambdaQueryWrapper<>();
        if (username != null && !username.isBlank()) {
            qw.like(AuditLog::getUsername, username.trim());
        }
        if (action != null && !action.isBlank()) {
            qw.eq(AuditLog::getAction, action.trim());
        }
        qw.orderByDesc(AuditLog::getCreateTime).orderByDesc(AuditLog::getId);
        return page(new Page<>(page, Math.min(Math.max(size, 1), 200)), qw);
    }

    private String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max);
    }
}
