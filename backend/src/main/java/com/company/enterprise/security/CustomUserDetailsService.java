package com.company.enterprise.security;

import com.company.enterprise.auth.entity.User;
import com.company.enterprise.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getName())
                .toList();

        return new EnterpriseUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                user.getLockedUntil(),
                authorities
        );
    }
}
