package com.company.enterprise.auth;

import com.company.enterprise.auth.dto.LoginRequest;
import com.company.enterprise.auth.dto.LoginResponse;
import com.company.enterprise.auth.entity.Role;
import com.company.enterprise.auth.entity.User;
import com.company.enterprise.auth.repository.UserRepository;
import com.company.enterprise.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginAuthenticatesUserAndReturnsAccessToken() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "admin@enterprise.local", "hashed", "Nguyễn", "An", true,
                Set.of(new Role(UUID.randomUUID(), "ADMIN")));
        LoginRequest request = new LoginRequest("admin@enterprise.local", "secret");

        when(userRepository.findByEmail("admin@enterprise.local")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("admin@enterprise.local", "secret")
        );
        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
        assertThat(response.user().email()).isEqualTo("admin@enterprise.local");
        assertThat(response.user().roles()).containsExactly("ADMIN");
    }
    @Test
    void failedLoginRecordsLockoutAttempt() {
        LoginRequest request = new LoginRequest("admin@enterprise.local", "wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationServiceException("invalid credentials"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthenticationServiceException.class);

        verify(userRepository).recordFailedLogin(
                eq("admin@enterprise.local"),
                eq(5),
                any(java.time.Instant.class),
                any(java.time.Instant.class)
        );
        verify(userRepository, never()).resetLoginFailures(anyString());
    }

    @Test
    void successfulLoginResetsFailedAttempts() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "admin@enterprise.local", "hashed", "Nguyễn", "An", true,
                Set.of(new Role(UUID.randomUUID(), "ADMIN")));
        LoginRequest request = new LoginRequest("admin@enterprise.local", "secret");

        when(userRepository.findByEmail("admin@enterprise.local")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        authService.login(request);

        verify(userRepository).resetLoginFailures("admin@enterprise.local");
    }

    @Test
    void changePasswordUpdatesHashWhenCurrentPasswordMatches() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "admin@enterprise.local", "old-hash", "Nguyễn", "An", true,
                Set.of(new Role(UUID.randomUUID(), "ADMIN")));

        when(userRepository.findByEmail("admin@enterprise.local")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("old-password", "old-hash")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "old-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-hash");

        authService.changePassword("admin@enterprise.local", "old-password", "new-password");

        assertThat(user.getPasswordHash()).isEqualTo("new-hash");
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsIncorrectCurrentPassword() {
        User user = new User(UUID.randomUUID(), "admin@enterprise.local", "old-hash", "Nguyễn", "An", true,
                Set.of(new Role(UUID.randomUUID(), "ADMIN")));

        when(userRepository.findByEmail("admin@enterprise.local")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> authService.changePassword("admin@enterprise.local", "wrong", "new-password"))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);

        verify(userRepository, never()).save(any(User.class));
    }

}

