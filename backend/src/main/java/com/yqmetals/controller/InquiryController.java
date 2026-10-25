package com.yqmetals.controller;

import com.yqmetals.entity.Inquiry;
import com.yqmetals.service.EmailService;
import com.yqmetals.service.InquiryService;
import com.yqmetals.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserService userService;
    private final EmailService emailService;
    @Value("${spring.mail.username:sophia@yqwiremesh.com}")
    private String notificationEmail;

    public InquiryController(InquiryService inquiryService, UserService userService, EmailService emailService) {
        this.inquiryService = inquiryService;
        this.userService = userService;
        this.emailService = emailService;
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
        inquiry.setMessage(req.message());
        inquiry.setStatus(com.yqmetals.common.enums.InquiryStatus.NEW);
        inquiryService.save(inquiry);
        emailService.sendInquiryNotification(notificationEmail,
                "New Inquiry from " + req.name(),
                "Name: " + req.name() + "\nCompany: " + req.company() + "\nEmail: " + req.email()
                        + "\nCountry: " + req.country() + "\nMessage: " + req.message());
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
    public ResponseEntity<?> assign(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        inquiryService.assign(id, body.get("assigneeId"));
        return ResponseEntity.ok(Map.of("message", "Assigned"));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> stats() {
        return inquiryService.getMonthlyStats();
    }

    public record SubmitRequest(
            @NotBlank String name,
            String company,
            String country,
            @NotBlank @Email String email,
            String phone,
            Long categoryId,
            @NotBlank String message
    ) {}
}
