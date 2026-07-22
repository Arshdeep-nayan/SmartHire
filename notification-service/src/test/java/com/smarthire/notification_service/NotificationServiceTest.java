package com.smarthire.notification_service;

import com.smarthire.notification_service.exception.NotificationNotFoundException;
import com.smarthire.notification_service.model.Notification;
import com.smarthire.notification_service.repository.NotificationRepository;
import com.smarthire.notification_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repo;

    @InjectMocks
    private NotificationService service;

    private Notification notification;

    @BeforeEach
    void setUp() {

        notification = new Notification();

        notification.setId("1");
        notification.setCandidateId(1);
        notification.setJobId(10);
        notification.setMessage("Application Submitted");
        notification.setType(
                Notification.NotificationType.APPLICATION_SUBMITTED);
        notification.setRead(false);
    }

    @Test
    void testCreateNotification() {

        when(repo.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notification saved =
                service.createNotification(notification);

        assertNotNull(saved);
        assertEquals(1, saved.getCandidateId());
        assertNotNull(saved.getCreatedAt());

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(repo).save(captor.capture());

        assertNotNull(captor.getValue().getCreatedAt());
    }

    @Test
    void testGetAllNotifications() {

        when(repo.findAll())
                .thenReturn(List.of(notification));

        List<Notification> result =
                service.getAllNotifications();

        assertEquals(1, result.size());
        assertEquals(notification, result.get(0));

        verify(repo).findAll();
    }

    @Test
    void testGetNotificationById_Success() {

        when(repo.findById("1"))
                .thenReturn(Optional.of(notification));

        Notification result =
                service.getNotificationById("1");

        assertEquals("1", result.getId());

        verify(repo).findById("1");
    }

    @Test
    void testGetNotificationById_NotFound() {

        when(repo.findById("1"))
                .thenReturn(Optional.empty());

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.getNotificationById("1"));

        assertEquals(
                "Notification not found with id : 1",
                exception.getMessage());

        verify(repo).findById("1");
    }
    @Test
    void testGetNotificationsByCandidateId_Success() {

        when(repo.findByCandidateIdOrderByCreatedAtDesc(1))
                .thenReturn(List.of(notification));

        List<Notification> result =
                service.getNotificationsByCandidateId(1);

        assertEquals(1, result.size());
        assertEquals(notification, result.get(0));

        verify(repo).findByCandidateIdOrderByCreatedAtDesc(1);
    }

    @Test
    void testGetNotificationsByCandidateId_NotFound() {

        when(repo.findByCandidateIdOrderByCreatedAtDesc(1))
                .thenReturn(List.of());

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.getNotificationsByCandidateId(1));

        assertEquals(
                "No notifications found for candidate id : 1",
                exception.getMessage());

        verify(repo).findByCandidateIdOrderByCreatedAtDesc(1);
    }

    @Test
    void testGetNotificationsByJobId_Success() {

        when(repo.findByJobIdOrderByCreatedAtDesc(10))
                .thenReturn(List.of(notification));

        List<Notification> result =
                service.getNotificationsByJobId(10);

        assertEquals(1, result.size());
        assertEquals(notification, result.get(0));

        verify(repo).findByJobIdOrderByCreatedAtDesc(10);
    }

    @Test
    void testGetNotificationsByJobId_NotFound() {

        when(repo.findByJobIdOrderByCreatedAtDesc(10))
                .thenReturn(List.of());

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.getNotificationsByJobId(10));

        assertEquals(
                "No notifications found for job id : 10",
                exception.getMessage());

        verify(repo).findByJobIdOrderByCreatedAtDesc(10);
    }

    @Test
    void testGetUnreadNotifications_Success() {

        notification.setRead(false);

        when(repo.findByReadFalse())
                .thenReturn(List.of(notification));

        List<Notification> result =
                service.getUnreadNotifications();

        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());

        verify(repo).findByReadFalse();
    }

    @Test
    void testGetUnreadNotifications_NotFound() {

        when(repo.findByReadFalse())
                .thenReturn(List.of());

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.getUnreadNotifications());

        assertEquals(
                "No unread notifications found",
                exception.getMessage());

        verify(repo).findByReadFalse();
    }
    @Test
    void testGetUnreadNotificationsByCandidateId_Success() {

        notification.setRead(false);

        when(repo.findByCandidateIdAndReadFalse(1))
                .thenReturn(List.of(notification));

        List<Notification> result =
                service.getUnreadNotificationsByCandidateId(1);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());

        verify(repo).findByCandidateIdAndReadFalse(1);
    }

    @Test
    void testGetUnreadNotificationsByCandidateId_NotFound() {

        when(repo.findByCandidateIdAndReadFalse(1))
                .thenReturn(List.of());

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.getUnreadNotificationsByCandidateId(1));

        assertEquals(
                "No unread notifications found for candidate id : 1",
                exception.getMessage());

        verify(repo).findByCandidateIdAndReadFalse(1);
    }

    @Test
    void testGetUnreadNotificationsByJobId_Success() {

        notification.setRead(false);

        when(repo.findByJobIdAndReadFalse(10))
                .thenReturn(List.of(notification));

        List<Notification> result =
                service.getUnreadNotificationsByJobId(10);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isRead());

        verify(repo).findByJobIdAndReadFalse(10);
    }

    @Test
    void testGetUnreadNotificationsByJobId_NotFound() {

        when(repo.findByJobIdAndReadFalse(10))
                .thenReturn(List.of());

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.getUnreadNotificationsByJobId(10));

        assertEquals(
                "No unread notifications found for job id : 10",
                exception.getMessage());

        verify(repo).findByJobIdAndReadFalse(10);
    }

    @Test
    void testMarkAsRead_Success() {

        notification.setRead(false);

        when(repo.findById("1"))
                .thenReturn(Optional.of(notification));

        when(repo.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notification result =
                service.markAsRead("1");

        assertTrue(result.isRead());

        verify(repo).findById("1");
        verify(repo).save(notification);
    }

    @Test
    void testMarkAsRead_NotFound() {

        when(repo.findById("1"))
                .thenReturn(Optional.empty());

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.markAsRead("1"));

        assertEquals(
                "Notification not found with id : 1",
                exception.getMessage());

        verify(repo).findById("1");
        verify(repo, never()).save(any());
    }

    @Test
    void testDeleteNotification_Success() {

        when(repo.existsById("1"))
                .thenReturn(true);

        service.deleteNotification("1");

        verify(repo).existsById("1");
        verify(repo).deleteById("1");
    }

    @Test
    void testDeleteNotification_NotFound() {

        when(repo.existsById("1"))
                .thenReturn(false);

        NotificationNotFoundException exception =
                assertThrows(
                        NotificationNotFoundException.class,
                        () -> service.deleteNotification("1"));

        assertEquals(
                "Notification not found with id : 1",
                exception.getMessage());

        verify(repo).existsById("1");
        verify(repo, never()).deleteById(anyString());
    }
}