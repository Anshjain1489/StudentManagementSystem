package in.springproject.service;

import in.springproject.dto.auth.LoginRequest;
import in.springproject.dto.auth.JwtResponse;
import in.springproject.dto.auth.ForgotPasswordRequest;
import in.springproject.dto.auth.ResetPasswordRequest;
import in.springproject.entity.Role;
import in.springproject.entity.User;
import in.springproject.entity.enums.RoleName;
import in.springproject.exception.BadRequestException;
import in.springproject.exception.ResourceNotFoundException;
import in.springproject.exception.TokenExpiredException;
import in.springproject.repository.RoleRepository;
import in.springproject.repository.UserRepository;
import in.springproject.security.JwtTokenProvider;
import in.springproject.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;
    @Mock private in.springproject.config.DataInitializer dataInitializer;

    @InjectMocks private AuthServiceImpl authService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:3000");

        testRole = Role.builder().name(RoleName.ROLE_STUDENT).build();
        testRole.setId(1L);

        testUser = User.builder()
            .username("john.doe")
            .email("john.doe@test.com")
            .password("encoded_password")
            .isActive(true)
            .isEmailVerified(true)
            .roles(Set.of(testRole))
            .build();
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("john.doe@test.com");
        loginRequest.setPassword("Password@123");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class))).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(anyString())).thenReturn("refresh-token");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        JwtResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getEmail()).isEqualTo("john.doe@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void shouldThrowExceptionForInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("john.doe@test.com");
        loginRequest.setPassword("wrong-password");

        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should send forgot password email")
    void shouldSendForgotPasswordEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("john.doe@test.com");

        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenReturn(testUser);
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());

        assertThatNoException().isThrownBy(() -> authService.forgotPassword(request));
        verify(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception for non-existent email on forgot password")
    void shouldThrowExceptionForNonExistentEmailOnForgotPassword() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("nonexistent@test.com");

        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.forgotPassword(request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception for expired reset token")
    void shouldThrowExceptionForExpiredToken() {
        testUser.setResetPasswordToken("valid-token");
        testUser.setResetPasswordTokenExpiry(LocalDateTime.now().minusHours(2)); // expired

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("valid-token");
        request.setNewPassword("NewPassword@123");

        when(userRepository.findByResetPasswordToken("valid-token")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.resetPassword(request))
            .isInstanceOf(TokenExpiredException.class)
            .hasMessageContaining("expired");
    }
}
