package com.company.enterprise.auth;

import com.company.enterprise.auth.dto.LoginRequest;
import com.company.enterprise.auth.dto.LoginResponse;
import com.company.enterprise.auth.dto.UserSummary;
import com.company.enterprise.auth.entity.User;
import com.company.enterprise.auth.repository.UserRepository;
import com.company.enterprise.auth.repository.RefreshTokenRepository;
import com.company.enterprise.auth.entity.RefreshToken;
import com.company.enterprise.auth.dto.RefreshTokenResponse;
import com.company.enterprise.auth.dto.ForgotPasswordResponse;
import com.company.enterprise.auth.repository.PasswordResetTokenRepository;
import com.company.enterprise.auth.entity.PasswordResetToken;
import com.company.enterprise.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class AuthService {
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(30);
    private static final Duration PASSWORD_RESET_DURATION = Duration.ofMinutes(30);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService,
                       PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException ex) {
            recordFailedLogin(request.email());
            throw ex;
        }

        userRepository.resetLoginFailures(request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));

        String refreshToken = issueRefreshToken(user);
        return new LoginResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationSeconds(),
                refreshToken,
                toUserSummary(user)
        );
    }

    @Transactional
    public RefreshTokenResponse refresh(String rawRefreshToken) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hashToken(rawRefreshToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        Instant now = Instant.now();
        if (!stored.isUsable(now)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!user.isEnabled() || user.isAccountLocked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User account is unavailable");
        }

        stored.revoke();
        refreshTokenRepository.save(stored);

        String newRefreshToken = issueRefreshToken(user);
        return new RefreshTokenResponse(
                jwtService.generateToken(user),
                "Bearer",
                jwtService.getExpirationSeconds(),
                newRefreshToken,
                toUserSummary(user)
        );
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        refreshTokenRepository.findByTokenHash(hashToken(rawRefreshToken))
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    private String issueRefreshToken(User user) {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        RefreshToken token = new RefreshToken(
                UUID.randomUUID(),
                user.getId(),
                hashToken(raw),
                Instant.now().plus(REFRESH_TOKEN_DURATION)
        );
        refreshTokenRepository.save(token);
        return raw;
    }

    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte value : digest) {
                hex.append(String.format("%02x", value));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    private UserSummary toUserSummary(User user) {
        return new UserSummary(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(role -> role.getName()).sorted().toList()
        );
    }


    @Transactional
    public ForgotPasswordResponse requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !user.isEnabled()) {
            return new ForgotPasswordResponse("If the account exists, a password reset token has been created.", null);
        }
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        passwordResetTokenRepository.save(new PasswordResetToken(UUID.randomUUID(), user.getId(), hashToken(raw), Instant.now().plus(PASSWORD_RESET_DURATION)));
        return new ForgotPasswordResponse("If the account exists, a password reset token has been created.", raw);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hashToken(rawToken))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token"));
        if (!token.isUsable(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token");
        }
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        token.markUsed();
        passwordResetTokenRepository.save(token);
    }

    @Transactional
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void recordFailedLogin(String email) {
        Instant now = Instant.now();
        userRepository.recordFailedLogin(
                email,
                MAX_FAILED_LOGIN_ATTEMPTS,
                now.plus(LOCK_DURATION),
                now
        );
    }
}
