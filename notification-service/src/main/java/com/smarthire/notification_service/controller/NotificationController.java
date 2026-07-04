package com.smarthire.notification_service.controller;

import com.smarthire.notification_service.model.Notification;
import com.smarthire.notification_service.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin
public class NotificationController {

    private final NotificationService service;

    public NotificationController(
            NotificationService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<Notification>
    createNotification(
            @RequestBody Notification notification) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        service.createNotification(
                                notification));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Notification>>
    getAllNotifications() {

        return ResponseEntity.ok(
                service.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification>
    getNotificationById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                service.getNotificationById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>>
    getNotificationsByUserId(
            @PathVariable int userId) {

        return ResponseEntity.ok(
                service.getNotificationsByUserId(
                        userId));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>>
    getUnreadNotifications() {

        return ResponseEntity.ok(
                service.getUnreadNotifications());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification>
    markAsRead(
            @PathVariable String id) {

        return ResponseEntity.ok(
                service.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteNotification(
            @PathVariable String id) {

        service.deleteNotification(id);

        return ResponseEntity.ok(
                "Notification deleted successfully");
    }
}