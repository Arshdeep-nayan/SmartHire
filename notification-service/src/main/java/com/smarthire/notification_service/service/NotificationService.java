package com.smarthire.notification_service.service;

import com.smarthire.notification_service.exception.NotificationNotFoundException;
import com.smarthire.notification_service.model.Notification;
import com.smarthire.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;
    public NotificationService(
            NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification createNotification(
            Notification notification) {

        notification.setCreatedAt(
                LocalDateTime.now());

        return repo.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return repo.findAll();
    }

    public Notification getNotificationById(
            String id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification not found with id : "
                                        + id));
    }

    public List<Notification> getNotificationsByUserId(
            int userId) {

        List<Notification> notifications =
                repo.findByUserIdOrderByCreatedAtDesc(
                        userId);

        if (notifications.isEmpty()) {
            throw new NotificationNotFoundException(
                    "No notifications found for user id : "
                            + userId);
        }

        return notifications;
    }

    public List<Notification> getUnreadNotifications() {

        List<Notification> notifications =
                repo.findByReadFalse();

        if (notifications.isEmpty()) {
            throw new NotificationNotFoundException(
                    "No unread notifications found");
        }

        return notifications;
    }

    public Notification markAsRead(
            String id) {

        Notification notification =
                repo.findById(id)
                        .orElseThrow(() ->
                                new NotificationNotFoundException(
                                        "Notification not found with id : "
                                                + id));

        notification.setRead(true);

        return repo.save(notification);
    }

    public void deleteNotification(
            String id) {

        if (!repo.existsById(id)) {
            throw new NotificationNotFoundException(
                    "Notification not found with id : "
                            + id);
        }

        repo.deleteById(id);
    }
}