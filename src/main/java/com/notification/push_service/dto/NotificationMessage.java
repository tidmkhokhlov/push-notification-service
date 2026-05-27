package com.notification.push_service.dto;

import java.util.UUID;

public class NotificationMessage {
    private UUID id;
    private String userId;
    private String message;

    public NotificationMessage() {}

    public NotificationMessage(UUID id, String userId, String message) {
        this.id = id;
        this.userId = userId;
        this.message = message;
    }

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
