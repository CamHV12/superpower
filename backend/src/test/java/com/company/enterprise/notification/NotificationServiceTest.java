package com.company.enterprise.notification;

import com.company.enterprise.auth.entity.User;
import com.company.enterprise.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Test
    void findMineReturnsOnlyCurrentUsersNotifications() {
        NotificationRepository repository = mock(NotificationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        User user = User.builder().email("admin@example.com").firstName("Admin").lastName("User").build();
        UUID userId = UUID.randomUUID();
        setId(user, userId);

        Notification notification = new Notification(userId, "SYSTEM", "Test", "Hello");
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(repository.findByUserIdOrderByCreatedAtDesc(eq(userId), any()))
                .thenReturn(new PageImpl<>(List.of(notification)));

        NotificationService service = new NotificationService(repository, userRepository);
        var result = service.findMine("admin@example.com", false, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Test", result.getContent().get(0).title());
        verify(repository).findByUserIdOrderByCreatedAtDesc(eq(userId), any());
    }

    @Test
    void markReadRejectsNotificationOwnedByAnotherUser() {
        NotificationRepository repository = mock(NotificationRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        User user = User.builder().email("admin@example.com").firstName("Admin").lastName("User").build();
        setId(user, UUID.randomUUID());
        UUID foreignUserId = UUID.randomUUID();
        Notification notification = new Notification(foreignUserId, "SYSTEM", "Test", "Hello");
        UUID notificationId = notification.getId();

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(repository.findById(notificationId)).thenReturn(Optional.of(notification));

        NotificationService service = new NotificationService(repository, userRepository);

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.markRead("admin@example.com", notificationId));
        assertNull(notification.getReadAt());
    }

    private static void setId(User user, UUID id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}