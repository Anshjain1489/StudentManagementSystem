ackage in.springproject.util;

/**
 * Application-wide constants used across controllers, services, and utilities.
 * This class is not instantiable.
 */
public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("AppConstants is a utility class and cannot be instantiated.");
    }

    // ── Pagination ─────────────────────────────────────────────────────────────
    /** Default page index (zero-based). */
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /** Default number of records per page. */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** Maximum allowed page size to prevent over-fetching. */
    public static final int MAX_PAGE_SIZE = 100;

    /** Default field used for sorting query results. */
    public static final String DEFAULT_SORT_BY = "id";

    /** Default sort direction. */
    public static final String DEFAULT_SORT_DIR = "asc";

    // ── File Upload ────────────────────────────────────────────────────────────
    /** Maximum file upload size: 10 MB. */
    public static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    /** Accepted MIME types for image uploads. */
    public static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/png", "image/gif"
    };

    /** Accepted MIME types for document uploads. */
    public static final String[] ALLOWED_DOC_TYPES = {
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };

    // ── Roles ──────────────────────────────────────────────────────────────────
    /** Spring Security authority name for admin users. */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /** Spring Security authority name for teacher users. */
    public static final String ROLE_TEACHER = "ROLE_TEACHER";

    /** Spring Security authority name for student users. */
    public static final String ROLE_STUDENT = "ROLE_STUDENT";

    // ── Grade Boundaries (percentage thresholds) ───────────────────────────────
    /** Minimum percentage for an A+ grade. */
    public static final double GPA_A_PLUS_MIN = 90.0;

    /** Minimum percentage for an A grade. */
    public static final double GPA_A_MIN = 80.0;

    /** Minimum percentage for a B+ grade. */
    public static final double GPA_B_PLUS_MIN = 70.0;

    /** Minimum percentage for a B grade. */
    public static final double GPA_B_MIN = 60.0;

    /** Minimum percentage for a C grade. */
    public static final double GPA_C_MIN = 50.0;

    /** Minimum percentage for a D grade. */
    public static final double GPA_D_MIN = 40.0;

    // ── Email Template Names ───────────────────────────────────────────────────
    /** Thymeleaf template path for welcome emails. */
    public static final String EMAIL_TEMPLATE_WELCOME = "email/welcome";

    /** Thymeleaf template path for password reset emails. */
    public static final String EMAIL_TEMPLATE_RESET_PASSWORD = "email/reset-password";

    /** Thymeleaf template path for fee reminder emails. */
    public static final String EMAIL_TEMPLATE_FEE_REMINDER = "email/fee-reminder";

    /** Thymeleaf template path for attendance alert emails. */
    public static final String EMAIL_TEMPLATE_ATTENDANCE_ALERT = "email/attendance-alert";

    /** Thymeleaf template path for exam reminder emails. */
    public static final String EMAIL_TEMPLATE_EXAM_REMINDER = "email/exam-reminder";
}
