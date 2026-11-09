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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    @Override
    public Map<String, Object> getMonthlyDailyStats(Integer year, Integer month) {
        LocalDate today = LocalDate.now();
        YearMonth current = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.from(today);
        YearMonth previous = current.minusMonths(1);
        int daysInMonth = current.lengthOfMonth();

        int[] daily = new int[daysInMonth];
        int currentTotal = 0;
        int previousTotal = 0;

        for (Inquiry q : findAllActive()) {
            if (q.getCreateTime() == null) {
                continue;
            }
            LocalDate created = q.getCreateTime().toLocalDate();
            YearMonth ym = YearMonth.from(created);
            if (ym.equals(current)) {
                daily[created.getDayOfMonth() - 1]++;
                currentTotal++;
            } else if (ym.equals(previous)) {
                previousTotal++;
            }
        }

        Double changePercent = null;
        if (previousTotal > 0) {
            changePercent = Math.round((currentTotal - previousTotal) * 1000.0 / previousTotal) / 10.0;
        } else if (currentTotal == 0) {
            changePercent = 0.0;
        }

        boolean isCurrentMonth = current.equals(YearMonth.from(today));
        int elapsedDays = isCurrentMonth ? today.getDayOfMonth() : daysInMonth;
        double dailyAverage = elapsedDays > 0
                ? Math.round(currentTotal * 100.0 / elapsedDays) / 100.0
                : 0.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", current.getYear());
        result.put("month", current.getMonthValue());
        result.put("daysInMonth", daysInMonth);
        result.put("daily", daily);
        result.put("total", currentTotal);
        result.put("previousTotal", previousTotal);
        result.put("changePercent", changePercent);
        result.put("elapsedDays", elapsedDays);
        result.put("dailyAverage", dailyAverage);
        return result;
    }

    @Override
    public List<Integer> getAvailableYears() {
        Set<Integer> years = new TreeSet<>(Comparator.reverseOrder());
        years.add(LocalDate.now().getYear());
        for (Inquiry q : findAllActive()) {
            if (q.getCreateTime() != null) {
                years.add(q.getCreateTime().getYear());
            }
        }
        return new ArrayList<>(years);
    }
}
