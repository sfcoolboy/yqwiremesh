package com.yqmetals.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yqmetals.common.enums.InquiryStatus;
import com.yqmetals.entity.Inquiry;
import com.yqmetals.mapper.InquiryMapper;
import com.yqmetals.service.InquiryService;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InquiryServiceImpl extends ServiceImpl<InquiryMapper, Inquiry> implements InquiryService {

    @Override
    public List<Inquiry> findByAssigneeId(Long assigneeId) {
        return list(new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getAssignedTo, assigneeId)
                .eq(Inquiry::getDeleted, false)
                .orderByDesc(Inquiry::getCreateTime));
    }

    @Override
    public List<Inquiry> findAllActive() {
        return list(new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getDeleted, false)
                .orderByDesc(Inquiry::getCreateTime));
    }

    @Override
    public void assign(Long inquiryId, Long assigneeId) {
        Inquiry inquiry = getById(inquiryId);
        if (inquiry != null) {
            inquiry.setAssignedTo(assigneeId);
            inquiry.setStatus(InquiryStatus.ASSIGNED);
            updateById(inquiry);
        }
    }

    @Override
    public Map<String, Object> getMonthlyStats() {
        List<Inquiry> all = findAllActive();
        Map<String, Integer> stats = new HashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 0; i < 12; i++) {
            YearMonth ym = YearMonth.from(now).minusMonths(i);
            String key = ym.toString();
            stats.put(key, 0);
        }

        for (Inquiry q : all) {
            String key = q.getCreateTime().toLocalDate().getYear() + "-" +
                    String.format("%02d", q.getCreateTime().toLocalDate().getMonthValue());
            stats.merge(key, 1, Integer::sum);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("monthly", stats);
        result.put("total", all.size());
        result.put("newThisMonth", stats.getOrDefault(now.getYear() + "-" +
                String.format("%02d", now.getMonthValue()), 0));
        return result;
    }
}
