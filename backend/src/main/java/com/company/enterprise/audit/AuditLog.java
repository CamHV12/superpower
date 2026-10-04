package com.company.enterprise.audit;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    private UUID id;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(name = "actor_email", length = 255)
    private String actorEmail;

    @Column(nullable = false, length = 20)
    private String action;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AuditLog() {}

    public AuditLog(UUID actorUserId, String actorEmail, String action, String path,
                    int statusCode, long durationMs, String ipAddress, String userAgent) {
        this.id = UUID.randomUUID();
        this.actorUserId = actorUserId;
        this.actorEmail = actorEmail;
        this.action = action;
        this.path = path;
        this.statusCode = statusCode;
        this.durationMs = durationMs;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public UUID getActorUserId() { return actorUserId; }
    public String getActorEmail() { return actorEmail; }
    public String getAction() { return action; }
    public String getPath() { return path; }
    public int getStatusCode() { return statusCode; }
    public long getDurationMs() { return durationMs; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Instant getCreatedAt() { return createdAt; }
}