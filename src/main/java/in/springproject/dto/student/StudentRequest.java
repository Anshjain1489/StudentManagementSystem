package in.springproject.dto.student;

import in.springproject.entity.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for creating or updating a student record.
 * Carries all mutable student profile attributes plus department association.
 */
@Data
public class StudentRequest {

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

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

    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private LocalDate enrollmentDate;
    private Integer currentSemester;
    private String bloodGroup;
    private String emergencyContact;

    @NotNull(message = "Department ID is required")
    private Long departmentId;
}
