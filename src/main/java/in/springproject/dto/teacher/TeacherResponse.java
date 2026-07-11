package in.springproject.dto.teacher;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.springproject.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing a teacher's profile returned by API responses.
 * Null fields are excluded from JSON serialization via {@link JsonInclude}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherResponse {

    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String qualification;
    private String specialization;
    private Integer experienceYears;
    private String photoUrl;
    private LocalDate joiningDate;
    private String address;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
}
