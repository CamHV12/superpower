package com.company.enterprise.notification;

import com.company.enterprise.auth.entity.User;
import com.company.enterprise.auth.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification create(UUID userId, String type, String title, String message) {
        return repository.save(new Notification(userId, type, title, message));
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> findMine(String email, boolean unreadOnly, Pageable pageable) {
        UUID userId = currentUser(email).getId();
        Page<Notification> notifications = unreadOnly
                ? repository.findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(userId, pageable)
                : repository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(NotificationResponse::from);
    }

    @Transactional(readOnly = true)
    public long countUnread(String email) {
        return repository.countByUserIdAndReadAtIsNull(currentUser(email).getId());
    }

    @Transactional
    public void markRead(String email, UUID notificationId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Không tìm thấy thông báo"));
        UUID userId = currentUser(email).getId();
        if (!notification.getUserId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("Không có quyền truy cập thông báo này");
        }
        notification.markRead();
    }

    @Transactional
    public void markAllRead(String email) {
        UUID userId = currentUser(email).getId();
        repository.findByUserIdAndReadAtIsNullOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(Notification::markRead);
    }

    private User currentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}