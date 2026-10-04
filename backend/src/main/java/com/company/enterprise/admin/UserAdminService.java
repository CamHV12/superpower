package com.company.enterprise.admin;

import com.company.enterprise.admin.dto.CreateUserRequest;
import com.company.enterprise.admin.dto.ResetUserPasswordRequest;
import com.company.enterprise.admin.dto.UpdateUserRequest;
import com.company.enterprise.admin.dto.UserAdminResponse;
import com.company.enterprise.auth.entity.Role;
import com.company.enterprise.auth.entity.User;
import com.company.enterprise.auth.repository.RoleRepository;
import com.company.enterprise.auth.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserAdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAdminService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserAdminResponse> findAll(String keyword, Pageable pageable) {
        Page<User> page = keyword == null || keyword.isBlank()
                ? userRepository.findAll(pageable)
                : userRepository.search(keyword.trim(), pageable);
        return page.map(this::toResponse);
    }

    public UserAdminResponse create(CreateUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email already exists");
        User user = User.builder().email(email).passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName().trim()).lastName(request.lastName().trim())
                .enabled(request.enabled()).roles(resolveRoles(request.roles())).build();
        return toResponse(userRepository.save(user));
    }

    public UserAdminResponse update(UUID id, UpdateUserRequest request) {
        User user = get(id);
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (!email.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(email))
            throw new IllegalArgumentException("Email already exists");
        user.setEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEnabled(request.enabled());
        if (request.roles() != null) {
            user.getRoles().clear();
            user.getRoles().addAll(resolveRoles(request.roles()));
        }
        return toResponse(userRepository.save(user));
    }

    public UserAdminResponse setEnabled(UUID id, boolean enabled) {
        User user = get(id);
        user.setEnabled(enabled);
        return toResponse(userRepository.save(user));
    }

    public UserAdminResponse unlock(UUID id) {
        User user = get(id);
        user.resetLoginLockout();
        return toResponse(userRepository.save(user));
    }

    public void resetPassword(UUID id, ResetUserPasswordRequest request) {
        User user = get(id);
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.resetLoginLockout();
        userRepository.save(user);
    }

    private User get(UUID id) { return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found")); }

    private Set<Role> resolveRoles(Set<String> names) {
        Set<Role> roles = new HashSet<>();
        if (names == null || names.isEmpty()) return roles;
        for (String name : names) {
            Role role = roleRepository.findByNameIgnoreCase(name.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name));
            roles.add(role);
        }
        return roles;
    }

    private UserAdminResponse toResponse(User user) {
        return new UserAdminResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(),
                user.isEnabled(), user.isAccountLocked(), user.getLockedUntil(), user.getFailedLoginAttempts(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()), user.getCreatedAt(), user.getUpdatedAt());
    }
}
