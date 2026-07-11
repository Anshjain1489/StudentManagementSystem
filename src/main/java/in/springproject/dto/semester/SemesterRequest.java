package in.springproject.dto.semester;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating or updating an academic semester.
 * The {@code isActive} flag marks the current running semester.
 */
@Data
public class SemesterRequest {

    @NotBlank(message = "Semester name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    /** When true, marks this semester as the currently active one. */
    private Boolean isActive;
}
