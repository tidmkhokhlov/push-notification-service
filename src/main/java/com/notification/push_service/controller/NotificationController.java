package com.notification.push_service.controller;

import com.notification.push_service.config.RabbitMQConfig;
import com.notification.push_service.dto.NotificationMessage;
import com.notification.push_service.dto.NotificationResponse;
import com.notification.push_service.dto.SendNotificationRequest;
import com.notification.push_service.model.Notification;
import com.notification.push_service.model.NotificationStatus;
import com.notification.push_service.repository.NotificationRepository;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository repo;
    private final RabbitTemplate rabbitTemplate;

    public NotificationController(NotificationRepository repo, RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationResponse sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        Notification notification = new Notification(
                request.getUserId(),
                request.getMessage(),
                NotificationStatus.PENDING
        );
        Notification saved = repo.save(notification);

        NotificationMessage message = new NotificationMessage(
                saved.getId(),
                saved.getUserId(),
                saved.getMessage()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        return toResponse(saved);
    }

    @GetMapping("/{id}")
    public NotificationResponse getNotificationStatus(@PathVariable UUID id) {
        Notification notification = repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Уведомление не найдено"));
        return toResponse(notification);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getUpdatedAt(),
                notification.getRetryCount(),
                notification.getErrorMessage()
        );
    }
}
