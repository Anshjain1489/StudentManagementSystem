package in.springproject.service.impl;

import in.springproject.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementation of {@link EmailService}.
 * Uses Spring Boot Starter Mail with Thymeleaf template processing.
 * All email transmissions are asynchronous.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:noreply@sms.edu}")
    private String fromEmail;

    @Async
    @Override
    public void sendWelcomeEmail(String to, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("appName", "Student Management System");

            String htmlContent;
            try {
                htmlContent = templateEngine.process("email/welcome", context);
            } catch (Exception e) {
                log.warn("Thymeleaf template 'email/welcome' not found, falling back to simple HTML");
                htmlContent = "<h3>Welcome to the Student Management System!</h3>" +
                        "<p>Dear " + name + ",</p>" +
                        "<p>Your account has been successfully created. You can now login to access your portal.</p>" +
                        "<p>Best regards,<br/>Administration</p>";
            }

            sendHtmlEmail(to, "Welcome to Student Management System", htmlContent);
            log.info("Welcome email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String name, String resetLink) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("resetLink", resetLink);

            String htmlContent;
            try {
                htmlContent = templateEngine.process("email/reset-password", context);
            } catch (Exception e) {
                log.warn("Thymeleaf template 'email/reset-password' not found, falling back to simple HTML");
                htmlContent = "<h3>Password Reset Request</h3>" +
                        "<p>Dear " + name + ",</p>" +
                        "<p>Please click the link below to reset your password. This link is valid for 1 hour.</p>" +
                        "<p><a href=\"" + resetLink + "\">Reset Password</a></p>" +
                        "<p>If you did not request a password reset, please ignore this email.</p>";
            }

            sendHtmlEmail(to, "Password Reset Request - Student Management System", htmlContent);
            log.info("Password reset email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendFeeReminderEmail(String to, String name, String feeDetails, String dueDate) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("feeDetails", feeDetails);
            context.setVariable("dueDate", dueDate);

            String htmlContent;
            try {
                htmlContent = templateEngine.process("email/fee-reminder", context);
            } catch (Exception e) {
                log.warn("Thymeleaf template 'email/fee-reminder' not found, falling back to simple HTML");
                htmlContent = "<h3>Fee Payment Reminder</h3>" +
                        "<p>Dear " + name + ",</p>" +
                        "<p>This is a reminder that you have an outstanding fee payment due on " + dueDate + ".</p>" +
                        "<p><strong>Details:</strong> " + feeDetails + "</p>" +
                        "<p>Please log in to the portal to settle your payment.</p>";
            }

            sendHtmlEmail(to, "Fee Payment Reminder - Student Management System", htmlContent);
            log.info("Fee reminder email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send fee reminder email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendAttendanceAlertEmail(String to, String name, String courseName, double percentage) {
        try {
            String htmlContent = "<h3>Low Attendance Warning</h3>" +
                    "<p>Dear " + name + ",</p>" +
                    "<p>Your attendance in the course <strong>" + courseName + "</strong> is currently " +
                    String.format("%.2f%%", percentage) + ", which falls below the minimum required threshold of 75%.</p>" +
                    "<p>Please ensure you attend the remaining classes to meet academic requirements.</p>";

            sendHtmlEmail(to, "Attendance Alert: " + courseName, htmlContent);
            log.info("Attendance warning email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send attendance alert email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendExamReminderEmail(String to, String name, String examName, String examDate) {
        try {
            String htmlContent = "<h3>Upcoming Exam Reminder</h3>" +
                    "<p>Dear " + name + ",</p>" +
                    "<p>This is a reminder that the exam <strong>" + examName + "</strong> is scheduled for " +
                    examDate + ".</p>" +
                    "<p>Please log in to check the schedule details, timetable, and instructions.</p>" +
                    "<p>Wishing you the best for your preparation.</p>";

            sendHtmlEmail(to, "Exam Reminder: " + examName, htmlContent);
            log.info("Exam reminder email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send exam reminder email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Simple email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage(), e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
    }
}
