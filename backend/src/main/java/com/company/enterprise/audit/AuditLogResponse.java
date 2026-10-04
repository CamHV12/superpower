package com.company.enterprise.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID actorUserId,
        String actorEmail,
        String action,
        String path,
        int statusCode,
        long durationMs,
        String ipAddress,
        String userAgent,
        Instant createdAt) {
    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(
                log.getId(), log.getActorUserId(), log.getActorEmail(), log.getAction(),
                log.getPath(), log.getStatusCode(), log.getDurationMs(), log.getIpAddress(),
                log.getUserAgent(), log.getCreatedAt());
    }
}