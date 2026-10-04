package com.company.enterprise.auth;

import com.company.enterprise.auth.dto.LoginResponse;
import com.company.enterprise.auth.dto.UserSummary;
import com.company.enterprise.auth.dto.ChangePasswordRequest;
import com.company.enterprise.auth.dto.ForgotPasswordResponse;
import com.company.enterprise.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired MockMvc mockMvc;

    @MockBean AuthService authService;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void loginReturnsNoStoreHeader() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse(
                "jwt-token", "Bearer", 3600, "refresh-token",
                new UserSummary(UUID.randomUUID(), "admin@enterprise.local", "Nguyen", "An", List.of("ADMIN"))
        ));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"admin@enterprise.local","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    void refreshReturnsNoStoreHeader() throws Exception {
        when(authService.refresh("refresh-token")).thenReturn(new com.company.enterprise.auth.dto.RefreshTokenResponse(
                "new-access", "Bearer", 3600, "new-refresh",
                new UserSummary(UUID.randomUUID(), "admin@enterprise.local", "Nguyen", "An", List.of("ADMIN"))
        ));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content("""
                                {"refreshToken":"refresh-token"}
                                """))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    void logoutRevokesRefreshToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType("application/json")
                        .content("""
                                {"refreshToken":"refresh-token"}
                                """))
                .andExpect(status().isNoContent());

        verify(authService).logout("refresh-token");
    }

    @Test
    void authenticatedUserCanChangePassword() throws Exception {
        mockMvc.perform(patch("/api/v1/auth/password")
                        .principal(new UsernamePasswordAuthenticationToken("admin@enterprise.local", null))
                        .contentType("application/json")
                        .content("""
                                {"currentPassword":"old-password","newPassword":"new-password"}
                                """))
                .andExpect(status().isNoContent());

        verify(authService).changePassword("admin@enterprise.local", "old-password", "new-password");
    }
    @Test
    void forgotPasswordReturnsNoStoreHeader() throws Exception {
        when(authService.requestPasswordReset("admin@enterprise.local"))
                .thenReturn(new ForgotPasswordResponse("If the account exists", "reset-token"));
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType("application/json")
                        .content("{\"email\":\"admin@enterprise.local\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("no-store")))
                .andExpect(jsonPath("$.message").value("If the account exists"));
    }

    @Test
    void resetPasswordReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType("application/json")
                        .content("{\"token\":\"reset-token\",\"newPassword\":\"new-password\"}"))
                .andExpect(status().isNoContent());
        verify(authService).resetPassword("reset-token", "new-password");
    }

}
