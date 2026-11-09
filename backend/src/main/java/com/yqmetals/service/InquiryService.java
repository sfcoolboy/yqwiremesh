package com.yqmetals.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yqmetals.entity.Inquiry;
import java.util.List;
import java.util.Map;

public interface InquiryService extends IService<Inquiry> {
    List<Inquiry> findByAssigneeId(Long assigneeId);
    List<Inquiry> findAllActive();
    void assign(Long inquiryId, Long assigneeId);
    Map<String, Object> getMonthlyStats();
    Map<String, Object> getMonthlyDailyStats(Integer year, Integer month);
    List<Integer> getAvailableYears();
}
