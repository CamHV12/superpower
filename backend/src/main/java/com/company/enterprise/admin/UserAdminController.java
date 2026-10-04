package com.company.enterprise.admin;

import com.company.enterprise.admin.dto.CreateUserRequest;
import com.company.enterprise.admin.dto.ResetUserPasswordRequest;
import com.company.enterprise.admin.dto.UpdateUserRequest;
import com.company.enterprise.admin.dto.UserAdminResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class UserAdminController {
    private final UserAdminService service;

    public UserAdminController(UserAdminService service) { this.service = service; }

    @GetMapping
    public Page<UserAdminResponse> findAll(@RequestParam(required = false) String keyword, Pageable pageable) {
        return service.findAll(keyword, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminResponse create(@Valid @RequestBody CreateUserRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public UserAdminResponse setEnabled(@PathVariable UUID id, @RequestParam boolean enabled) {
        return service.setEnabled(id, enabled);
    }

    @PostMapping("/{id}/unlock")
    public UserAdminResponse unlock(@PathVariable UUID id) {
        return service.unlock(id);
    }

    @PostMapping("/{id}/reset-password")
    public void resetPassword(@PathVariable UUID id, @Valid @RequestBody ResetUserPasswordRequest request) {
        service.resetPassword(id, request);
    }
}
