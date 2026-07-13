package in.springproject.service.impl;

import in.springproject.dto.auth.*;
import in.springproject.entity.User;
import in.springproject.exception.BadRequestException;
import in.springproject.exception.ResourceNotFoundException;
import in.springproject.exception.TokenExpiredException;
import in.springproject.repository.UserRepository;
import in.springproject.security.JwtTokenProvider;
import in.springproject.service.AuthService;
import in.springproject.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AuthService} that handles all authentication workflows
 * including JWT login, token refresh, password reset, and logout.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final in.springproject.config.DataInitializer dataInitializer;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * {@inheritDoc}
     * <p>Authenticates via Spring Security, generates JWT tokens, and persists the refresh token.</p>
     */
    @Override
    public JwtResponse login(LoginRequest request) {
        // Seed default roles and admin dynamically on first login request
        try {
            dataInitializer.run();
        } catch (Exception e) {
            log.error("Error during dynamic database seeding: ", e);
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsernameOrEmail());

        User user = userRepository.findByEmail(request.getUsernameOrEmail())
            .or(() -> userRepository.findByUsername(request.getUsernameOrEmail()))
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRefreshToken(refreshToken);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in: {}", user.getEmail());

        return JwtResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
            .build();
    }

    /**
     * {@inheritDoc}
     * <p>Validates the stored refresh token, issues a new token pair, and rotates the refresh token.</p>
     */
    @Override
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
            .orElseThrow(() -> new TokenExpiredException("Invalid or expired refresh token"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return JwtResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .userId(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
            .build();
    }

    /**
     * {@inheritDoc}
     * <p>Generates a one-time reset token (valid 1 hour) and emails it to the user.</p>
     */
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + request.getEmail()));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String resetLink = frontendUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
        log.info("Password reset email sent to: {}", user.getEmail());
    }

    /**
     * {@inheritDoc}
     * <p>Validates the reset token expiry, encodes the new password, and clears all tokens.</p>
     */
    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
            .orElseThrow(() -> new BadRequestException("Invalid password reset token"));

        if (user.getResetPasswordTokenExpiry() == null ||
            user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Password reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        user.setRefreshToken(null);
        userRepository.save(user);
        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    /**
     * {@inheritDoc}
     * <p>Verifies the current password matches before encoding and saving the new one.</p>
     */
    @Override
    public void changePassword(ChangePasswordRequest request, String currentUserEmail) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        User user = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setRefreshToken(null);
        userRepository.save(user);
        log.info("Password changed for user: {}", currentUserEmail);
    }

    /**
     * {@inheritDoc}
     * <p>Clears the refresh token so no further token refresh is possible.</p>
     */
    @Override
    public void logout(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepository.save(user);
            log.info("User logged out: {}", email);
        });
    }
}
