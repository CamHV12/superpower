package com.company.enterprise.notification;

import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public Page<NotificationResponse> findMine(
            Authentication authentication,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Pageable pageable) {
        return service.findMine(authentication.getName(), unreadOnly, pageable);
    }

    @GetMapping("/unread-count")
    public long unreadCount(Authentication authentication) {
        return service.countUnread(authentication.getName());
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void markRead(Authentication authentication, @PathVariable UUID id) {
        service.markRead(authentication.getName(), id);
    }

    @PatchMapping("/read-all")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void markAllRead(Authentication authentication) {
        service.markAllRead(authentication.getName());
    }
}