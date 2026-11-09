package com.yqmetals.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    /** 数据库未配置收件人时的兜底。 */
    private static final String DEFAULT_MAIL_ACCOUNT = "sales@yqwiremesh.com";

    private final SettingService settingService;

    public EmailService(SettingService settingService) {
        this.settingService = settingService;
    }

    /**
     * 发送询盘通知。收件人始终以管理后台的 mail.recipient 为准，
     * 不再混用 application.yml 里的 spring.mail.username。
     */
    public void sendInquiryNotification(String subject, String body) {
        sendEmail(recipient(), subject, body);
    }

    /** 先读数据库配置，没有时兜底到常量，避免空收件人静默跳过。 */
    private String recipient() {
        String recipient = settingService.get("mail.recipient", DEFAULT_MAIL_ACCOUNT);
        return (recipient != null && !recipient.isBlank()) ? recipient : DEFAULT_MAIL_ACCOUNT;
    }

    public boolean sendEmail(String recipient, String subject, String body) {
        String host = smtpHost();
        Integer port = smtpPort();
        String username = smtpUsername();
        String password = smtpPassword();
        boolean smtpSsl = smtpSsl();
        boolean startTls = startTls();

        if (recipient == null || recipient.isBlank()) {
            log.warn("Notification recipient is not configured; skip sending from {}", username);
            return false;
        }
        if (password.isBlank()) {
            log.warn("SMTP password is not configured; skip sending email from {} to {}", username, recipient);
            return false;
        }

        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(host);
            sender.setPort(port);
            sender.setUsername(username);
            sender.setPassword(password);

            Properties props = new Properties();
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.ssl.enable", String.valueOf(smtpSsl));
            props.setProperty("mail.smtp.starttls.enable", String.valueOf(startTls));
            sender.setJavaMailProperties(props);

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress());
            msg.setTo(recipient.trim());
            msg.setSubject(subject);
            msg.setText(body);
            sender.send(msg);
            log.info("Email sent from {} to {}", username, recipient);
            return true;
        } catch (Exception e) {
            log.warn("Failed to send email from {} to {} (host={}, port={}): {}",
                    username, recipient, host, port, e.getMessage());
            return false;
        }
    }

    /** 新命名空间优先，旧扁平 key 兼容已有数据。 */
    private String smtpHost() {
        return settingService.get("mail.smtp.host", settingService.get("mail.host", "smtp.gmail.com"));
    }

    private Integer smtpPort() {
        String portStr = settingService.get("mail.smtp.port", settingService.get("mail.port", "587"));
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            log.warn("Invalid SMTP port '{}', using 587", portStr);
            return 587;
        }
    }

    private String smtpUsername() {
        return settingService.get("mail.smtp.username", settingService.get("mail.username", DEFAULT_MAIL_ACCOUNT)).trim();
    }

    private String smtpPassword() {
        return settingService.get("mail.smtp.password", settingService.get("mail.password", ""));
    }

    private boolean smtpSsl() {
        return "true".equalsIgnoreCase(settingService.get("mail.smtp.ssl", "false"));
    }

    private boolean startTls() {
        return "true".equalsIgnoreCase(settingService.get("mail.smtp.starttls", "true"));
    }

    private String fromAddress() {
        String from = settingService.get("mail.from", "");
        return from.isBlank() ? smtpUsername() : from;
    }
}
