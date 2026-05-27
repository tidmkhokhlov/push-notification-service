package com.notification.push_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendNotificationRequest {

    @NotBlank(message = "userId не может быть пустым")
    private String userId;

    @NotBlank(message = "message не может быть пустым")
    @Size(max = 500, message = "Сообщение не должно превышать 500 символов")
    private String message;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
