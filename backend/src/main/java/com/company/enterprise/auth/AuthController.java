package com.company.enterprise.auth;

import com.company.enterprise.auth.dto.LoginRequest;
import com.company.enterprise.auth.dto.LoginResponse;
import com.company.enterprise.auth.dto.ChangePasswordRequest;
import com.company.enterprise.auth.dto.RefreshTokenRequest;
import com.company.enterprise.auth.dto.RefreshTokenResponse;
import com.company.enterprise.auth.dto.ForgotPasswordRequest;
import com.company.enterprise.auth.dto.ForgotPasswordResponse;
import com.company.enterprise.auth.dto.ResetPasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(Authentication authentication,
                               @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(authentication.getName(), request.currentPassword(), request.newPassword());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(authService.requestPasswordReset(request.email()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(authService.login(request));
    }
}