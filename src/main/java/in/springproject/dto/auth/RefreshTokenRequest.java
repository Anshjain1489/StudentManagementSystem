package in.springproject.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for requesting a new access token using an existing refresh token.
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
