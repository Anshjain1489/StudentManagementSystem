package in.springproject.dto.teacher;

import in.springproject.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating or updating a teacher record.
 * Includes professional details, contact information and department assignment.
 */
@Data
public class TeacherRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    private String phone;

    private Gender gender;
    private LocalDate dateOfBirth;
    private String qualification;
    private String specialization;
    private Integer experienceYears;
    private LocalDate joiningDate;
    private String address;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
