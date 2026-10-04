package com.company.enterprise.security;

import com.company.enterprise.auth.entity.User;
import com.company.enterprise.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

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

        String[] authorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .toArray(String[]::new);

        return new EnterpriseUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                java.util.Arrays.asList(user.getRoles().stream()
                        .map(role -> (org.springframework.security.core.GrantedAuthority) () -> "ROLE_" + role.getName())
                        .toArray(org.springframework.security.core.GrantedAuthority[]::new))
        );
    }
}