package in.springproject.service;

/**
 * Service interface for sending application emails.
 * All methods are expected to be executed asynchronously.
 */
public interface EmailService {

    /**
     * Send a welcome email to a newly registered user.
     *
     * @param to   recipient email address
     * @param name recipient display name
     */
    void sendWelcomeEmail(String to, String name);

    /**
     * Send a password-reset email containing a time-limited reset link.
     *
     * @param to        recipient email address
     * @param name      recipient display name
     * @param resetLink one-time password reset URL (expires in 1 hour)
     */
    void sendPasswordResetEmail(String to, String name, String resetLink);

    /**
     * Send a fee payment reminder.
     *
     * @param to         recipient email address
     * @param name       recipient display name
     * @param feeDetails human-readable fee description and amount
     * @param dueDate    formatted due date string
     */
    void sendFeeReminderEmail(String to, String name, String feeDetails, String dueDate);

    /**
     * Send a low-attendance alert when attendance drops below threshold.
     *
     * @param to         recipient email address
     * @param name       recipient display name
     * @param courseName course with low attendance
     * @param percentage current attendance percentage (0–100)
     */
    void sendAttendanceAlertEmail(String to, String name, String courseName, double percentage);

    /**
     * Send an upcoming exam reminder.
     *
     * @param to        recipient email address
     * @param name      recipient display name
     * @param examName  name/title of the exam
     * @param examDate  formatted exam date/time string
     */
    void sendExamReminderEmail(String to, String name, String examName, String examDate);

    /**
     * Send a plain-text email with custom subject and body.
     *
     * @param to      recipient email address
     * @param subject email subject line
     * @param body    plain-text email body
     */
    void sendSimpleEmail(String to, String subject, String body);
}
