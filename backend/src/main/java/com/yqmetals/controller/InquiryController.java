package com.yqmetals.controller;

import com.yqmetals.entity.Inquiry;
import com.yqmetals.entity.User;
import com.yqmetals.security.AuditLogInterceptor;
import com.yqmetals.service.AuditLogService;
import com.yqmetals.service.EmailService;
import com.yqmetals.service.InquiryService;
import com.yqmetals.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserService userService;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    public InquiryController(InquiryService inquiryService, UserService userService, EmailService emailService,
                             AuditLogService auditLogService) {
        this.inquiryService = inquiryService;
        this.userService = userService;
        this.emailService = emailService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/public/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody SubmitRequest req) {
        Inquiry inquiry = new Inquiry();
        inquiry.setName(req.name());
        inquiry.setCompany(req.company());
        inquiry.setCountry(req.country());
        inquiry.setEmail(req.email());
        inquiry.setPhone(req.phone());
        inquiry.setCategoryId(req.categoryId());
        inquiry.setInterestedProduct(req.interestedProduct());
        inquiry.setMessage(req.message());
        inquiry.setStatus(com.yqmetals.common.enums.InquiryStatus.NEW);
        inquiryService.save(inquiry);
        emailService.sendInquiryNotification("新询盘通知 - " + inquiry.getName(), buildInquiryEmailBody(inquiry));
        return ResponseEntity.ok(Map.of("id", inquiry.getId(), "message", "Inquiry submitted"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Inquiry> getAll() {
        return inquiryService.findAllActive();
    }

    @GetMapping("/mine")
    public List<Inquiry> getMyInquiries(Authentication auth) {
        return inquiryService.findByAssigneeId(Long.parseLong(auth.getName()));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assign(@PathVariable Long id, @RequestBody Map<String, Long> body,
                                    HttpServletRequest request, Authentication auth) {
        Long assigneeId = body.get("assigneeId");
        Inquiry before = inquiryService.getById(id);
        if (before == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "询盘不存在"));
        }
        if (assigneeId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "请选择业务员"));
        }
        User assignee = userService.getById(assigneeId);
        if (assignee == null || assignee.getRole() == null
                || !"SALES".equals(assignee.getRole().name())
                || Boolean.FALSE.equals(assignee.getActive())) {
            return ResponseEntity.badRequest().body(Map.of("error", "所选业务员不存在或已停用"));
        }
        String workEmail = assignee.getWorkEmail() == null ? "" : assignee.getWorkEmail().trim();
        if (workEmail.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "所选业务员未配置工作邮箱"));
        }
        Long previousAssignee = before != null ? before.getAssignedTo() : null;

        inquiryService.assign(id, assigneeId);

        String action = previousAssignee == null ? "分配询单" : "重新分配询单";
        String detail;
        if (previousAssignee == null) {
            detail = "分配给 " + displayName(assigneeId);
        } else {
            detail = "从 " + displayName(previousAssignee) + " 重新分配给 " + displayName(assigneeId);
        }
        try {
            auditLogService.record(
                    auth != null ? auth.getName() : null,
                    action,
                    "/api/inquiries/" + id + "/assign",
                    detail,
                    AuditLogInterceptor.clientIp(request),
                    request.getHeader("User-Agent"),
                    true);
        } catch (Exception ignored) {
            // Never block assignment because of audit logging.
        }

        String emailSubject = (previousAssignee == null ? "新询盘分配通知 - " : "询盘重新分配通知 - ")
                + before.getName();
        boolean emailSent = emailService.sendEmail(workEmail, emailSubject, buildInquiryEmailBody(before));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Assigned");
        result.put("emailSent", emailSent);
        if (!emailSent) {
            result.put("emailWarning", "请检查 SMTP 密码/授权码和业务员工作邮箱");
        }
        return ResponseEntity.ok(result);
    }

    private String buildInquiryEmailBody(Inquiry inquiry) {
        return "询盘编号：" + value(inquiry.getId())
                + "\n询盘日期：" + formatInquiryDate(inquiry)
                + "\n客户姓名：" + value(inquiry.getName())
                + "\n客户所在公司：" + value(inquiry.getCompany())
                + "\n客户邮箱：" + value(inquiry.getEmail())
                + "\n客户电话：" + value(inquiry.getPhone())
                + "\n客户所在国家/地区：" + value(inquiry.getCountry())
                + "\n客户感兴趣产品：" + value(inquiry.getInterestedProduct())
                + "\n询盘内容：" + value(inquiry.getMessage());
    }

    private String formatInquiryDate(Inquiry inquiry) {
        return inquiry.getCreateTime() == null
                ? "—"
                : inquiry.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private String value(Object value) {
        return value == null || value.toString().isBlank() ? "—" : value.toString();
    }

    private String displayName(Long userId) {
        if (userId == null) {
            return "未分配";
        }
        User user = userService.getById(userId);
        if (user == null) {
            return "用户#" + userId;
        }
        return user.getDisplayName() != null && !user.getDisplayName().isBlank()
                ? user.getDisplayName()
                : user.getUsername();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> stats() {
        return inquiryService.getMonthlyStats();
    }

    @GetMapping("/stats/current-month")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> currentMonthStats(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {
        return inquiryService.getMonthlyDailyStats(year, month);
    }

    @GetMapping("/stats/years")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Integer> availableYears() {
        return inquiryService.getAvailableYears();
    }

    public record SubmitRequest(
            @NotBlank String name,
            String company,
            String country,
            @NotBlank @Email String email,
            String phone,
            Long categoryId,
            String interestedProduct,
            @NotBlank String message
    ) {}
}
