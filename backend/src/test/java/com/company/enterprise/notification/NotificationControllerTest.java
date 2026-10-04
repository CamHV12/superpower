package com.company.enterprise.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import com.company.enterprise.security.JwtAuthenticationFilter;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean NotificationService service;

    @Test
    void listsCurrentUserNotifications() throws Exception {
        Notification notification = new Notification(UUID.randomUUID(), "SYSTEM", "Test", "Hello");
        when(service.findMine(eq("admin@example.com"), eq(false), any()))
                .thenReturn(new PageImpl<>(List.of(NotificationResponse.from(notification))));

        mockMvc.perform(get("/api/v1/notifications")
                        .principal(new UsernamePasswordAuthenticationToken("admin@example.com", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test"));
    }

    @Test
    void returnsUnreadCount() throws Exception {
        when(service.countUnread("admin@example.com")).thenReturn(3L);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .principal(new UsernamePasswordAuthenticationToken("admin@example.com", null)))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void marksNotificationRead() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/api/v1/notifications/" + id + "/read")
                        .principal(new UsernamePasswordAuthenticationToken("admin@example.com", null)))
                .andExpect(status().isNoContent());

        verify(service).markRead("admin@example.com", id);
    }

    @Test
    void marksAllNotificationsRead() throws Exception {
        mockMvc.perform(patch("/api/v1/notifications/read-all")
                        .principal(new UsernamePasswordAuthenticationToken("admin@example.com", null)))
                .andExpect(status().isNoContent());

        verify(service).markAllRead("admin@example.com");
    }
}
