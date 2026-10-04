package com.company.enterprise.auth.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {}

    public User(UUID id, String email, String passwordHash, String firstName, String lastName,
                boolean enabled, Set<Role> roles) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.roles = roles;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public boolean isEnabled() { return enabled; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void resetLoginLockout() { this.failedLoginAttempts = 0; this.lockedUntil = null; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public Instant getLockedUntil() { return lockedUntil; }
    public boolean isAccountLocked() {
        return lockedUntil != null && lockedUntil.isAfter(Instant.now());
    }
    public Set<Role> getRoles() { return roles; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public static Builder builder() { return new Builder(); }
    public static final class Builder {
        private String email;
        private String passwordHash;
        private String firstName;
        private String lastName;
        private boolean enabled = true;
        private Set<Role> roles = new HashSet<>();
        public Builder email(String value) { this.email = value; return this; }
        public Builder passwordHash(String value) { this.passwordHash = value; return this; }
        public Builder firstName(String value) { this.firstName = value; return this; }
        public Builder lastName(String value) { this.lastName = value; return this; }
        public Builder enabled(boolean value) { this.enabled = value; return this; }
        public Builder roles(Set<Role> value) { this.roles = value; return this; }
        public User build() { return new User(null, email, passwordHash, firstName, lastName, enabled, roles); }
    }
}
