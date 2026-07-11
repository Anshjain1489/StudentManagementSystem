package in.springproject.service;

import in.springproject.dto.auth.*;

/**
 * Service interface for Authentication operations.
 * Handles login, token refresh, password management, and logout.
 */
public interface AuthService {

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param request login credentials (username/email + password)
     * @return JWT access and refresh tokens with user info
     */
    JwtResponse login(LoginRequest request);

    /**
     * Issues a new access token using a valid refresh token.
     *
     * @param request contains the refresh token
     * @return new JWT access and refresh tokens
     */
    JwtResponse refreshToken(RefreshTokenRequest request);

    /**
     * Sends a password reset link to the user's registered email.
     *
     * @param request contains the user's email address
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Resets the user's password using a valid reset token.
     *
     * @param request contains reset token and new password
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Changes the password for the currently authenticated user.
     *
     * @param request           contains current and new passwords
     * @param currentUserEmail  email of the authenticated user
     */
    void changePassword(ChangePasswordRequest request, String currentUserEmail);

    /**
     * Logs out the user by invalidating their refresh token.
     *
     * @param email email of the user to log out
     */
    void logout(String email);
}
