package in.springproject.dto.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for user registration request with full validation constraints.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain uppercase, lowercase, digit and special character"
    )
    private String password;

    /** Role to assign: ADMIN, TEACHER, or STUDENT. */
    private String role;
}
