package com.company.enterprise.auth;

import com.company.enterprise.auth.dto.LoginResponse;
import com.company.enterprise.auth.dto.UserSummary;
import com.company.enterprise.auth.dto.ChangePasswordRequest;
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
                "jwt-token", "Bearer", 3600,
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
}
