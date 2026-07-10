package com.smarthire.notification_service.service;

import com.smarthire.notification_service.exception.NotificationNotFoundException;
import com.smarthire.notification_service.model.Notification;
import com.smarthire.notification_service.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(
            NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification createNotification(
            Notification notification) {

        log.info("Creating notification for user id: {}", notification.getUserId());

        notification.setCreatedAt(
                LocalDateTime.now());

        Notification savedNotification = repo.save(notification);

        log.info("Notification created successfully with id: {}", savedNotification.getId());

        return savedNotification;
    }

    public List<Notification> getAllNotifications() {

        log.info("Fetching all notifications");

        List<Notification> notifications = repo.findAll();

        log.info("Fetched {} notifications", notifications.size());

        return notifications;
    }

    public Notification getNotificationById(
            String id) {

        log.info("Fetching notification with id: {}", id);

        Notification notification = repo.findById(id)
                .orElseThrow(() ->
                        new NotificationNotFoundException(
                                "Notification not found with id : "
                                        + id));

        log.info("Notification fetched successfully with id: {}", id);

        return notification;
    }

    public List<Notification> getNotificationsByUserId(
            int userId) {

        log.info("Fetching notifications for user id: {}", userId);

        List<Notification> notifications =
                repo.findByUserIdOrderByCreatedAtDesc(
                        userId);

        if (notifications.isEmpty()) {
            throw new NotificationNotFoundException(
                    "No notifications found for user id : "
                            + userId);
        }

        log.info("Fetched {} notifications for user id: {}", notifications.size(), userId);

        return notifications;
    }

    public List<Notification> getUnreadNotifications() {

        log.info("Fetching unread notifications");

        List<Notification> notifications =
                repo.findByReadFalse();

        if (notifications.isEmpty()) {
            throw new NotificationNotFoundException(
                    "No unread notifications found");
        }

        log.info("Fetched {} unread notifications", notifications.size());

        return notifications;
    }

    public Notification markAsRead(
            String id) {

        log.info("Marking notification as read with id: {}", id);

        Notification notification =
                repo.findById(id)
                        .orElseThrow(() ->
                                new NotificationNotFoundException(
                                        "Notification not found with id : "
                                                + id));

        notification.setRead(true);

        Notification updatedNotification = repo.save(notification);

        log.info("Notification marked as read successfully with id: {}", id);

        return updatedNotification;
    }

    public void deleteNotification(
            String id) {

        log.info("Deleting notification with id: {}", id);

        if (!repo.existsById(id)) {
            throw new NotificationNotFoundException(
                    "Notification not found with id : "
                            + id);
        }

        repo.deleteById(id);

        log.info("Notification deleted successfully with id: {}", id);
    }
}