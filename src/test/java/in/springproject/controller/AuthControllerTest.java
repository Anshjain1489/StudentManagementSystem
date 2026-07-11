ackage in.springproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.springproject.dto.auth.JwtResponse;
import in.springproject.dto.auth.LoginRequest;
import in.springproject.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuthService authService;

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return JWT on valid credentials")
    void shouldReturnJwtOnValidLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("admin@sms.edu");
        request.setPassword("Admin@123");

        JwtResponse jwtResponse = JwtResponse.builder()
            .accessToken("test-access-token")
            .refreshToken("test-refresh-token")
            .userId(1L)
            .email("admin@sms.edu")
            .roles(List.of("ROLE_ADMIN"))
            .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("test-access-token"))
            .andExpect(jsonPath("$.data.email").value("admin@sms.edu"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 400 for empty credentials")
    void shouldReturn400ForEmptyCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        // Empty credentials - should fail validation

        mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 401 for bad credentials")
    void shouldReturn401ForBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsernameOrEmail("test@test.com");
        request.setPassword("WrongPassword@1");

        when(authService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false));
    }
}
