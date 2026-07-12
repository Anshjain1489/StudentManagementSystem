package in.springproject.scheduler;

import in.springproject.entity.Fees;
import in.springproject.repository.FeesRepository;
import in.springproject.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled tasks for fee payment alerts and reminders.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FeeReminderScheduler {

    private final FeesRepository feesRepository;
    private final EmailService emailService;

    /**
     * Daily check at 9:00 AM.
     * Selects all active pending fees that are due in exactly 7 days and sends an email reminder.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendUpcomingFeeReminders() {
        log.info("Executing scheduled task: sendUpcomingFeeReminders");
        LocalDate targetDate = LocalDate.now().plusDays(7);
        
        List<Fees> upcomingFees = feesRepository.findDueBetween(LocalDate.now(), targetDate);
        for (Fees fee : upcomingFees) {
            if (!Boolean.TRUE.equals(fee.getIsPaid()) && !Boolean.TRUE.equals(fee.getDeleted())) {
                try {
                    String studentName = fee.getStudent().getFirstName() + " " + fee.getStudent().getLastName();
                    String feeDetails = fee.getFeeType().name() + " Fee: $" + fee.getAmount();
                    emailService.sendFeeReminderEmail(
                        fee.getStudent().getEmail(),
                        studentName,
                        feeDetails,
                        fee.getDueDate().toString()
                    );
                } catch (Exception e) {
                    log.error("Failed to send scheduled fee reminder for student {} on fee ID {}", fee.getStudent().getEmail(), fee.getId(), e);
                }
            }
        }
    }

    /**
     * Every Monday at 10:00 AM.
     * Selects all overdue unpaid fees and sends alerts.
     */
    @Scheduled(cron = "0 0 10 ? * MON")
    public void sendOverdueFeeReminders() {
        log.info("Executing scheduled task: sendOverdueFeeReminders");
        
        List<Fees> overdueFees = feesRepository.findOverdueFees(LocalDate.now());
        for (Fees fee : overdueFees) {
            if (!Boolean.TRUE.equals(fee.getIsPaid()) && !Boolean.TRUE.equals(fee.getDeleted())) {
                try {
                    String studentName = fee.getStudent().getFirstName() + " " + fee.getStudent().getLastName();
                    String subject = "Overdue Fee Notice - Student Management System";
                    String body = "Dear " + studentName + ",\n\n" +
                            "This is an urgent notice that your fee payment for " + fee.getFeeType().name() +
                            " in the amount of $" + fee.getAmount() + " is overdue since " + fee.getDueDate() + ".\n\n" +
                            "Please clear this balance immediately on the student portal to avoid penalties or registration hold.\n\n" +
                            "Regards,\nFinance Office";
                    emailService.sendSimpleEmail(fee.getStudent().getEmail(), subject, body);
                } catch (Exception e) {
                    log.error("Failed to send scheduled overdue notice for student {} on fee ID {}", fee.getStudent().getEmail(), fee.getId(), e);
                }
            }
        }
    }
}
