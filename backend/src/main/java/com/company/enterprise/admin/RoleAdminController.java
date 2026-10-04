package com.company.enterprise.admin;

import com.company.enterprise.admin.dto.RoleResponse;
import com.company.enterprise.auth.repository.RoleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/roles")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class RoleAdminController {
    private final RoleRepository roleRepository;

    public RoleAdminController(RoleRepository roleRepository) { this.roleRepository = roleRepository; }

    @GetMapping
    public List<RoleResponse> findAll() {
        return roleRepository.findAll().stream().map(role -> new RoleResponse(role.getId(), role.getName())).toList();
    }
}