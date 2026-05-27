package com.notification.push_service.dto;

import com.notification.push_service.model.NotificationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponse {
    private UUID id;
    private String userId;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer retryCount;
    private String errorMessage;

    public NotificationResponse(UUID id, String userId, String message, NotificationStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, Integer retryCount, String errorMessage) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
    }

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Integer getRetryCount() { return retryCount; }
    public String getErrorMessage() { return errorMessage; }
}
