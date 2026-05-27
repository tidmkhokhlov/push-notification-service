package com.notification.push_service.service;

import com.notification.push_service.config.RabbitMQConfig;
import com.notification.push_service.dto.NotificationMessage;
import com.notification.push_service.model.Notification;
import com.notification.push_service.model.NotificationStatus;
import com.notification.push_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class NotificationWorker {
    private static final Logger logger = LoggerFactory.getLogger(NotificationWorker.class);
    private final NotificationRepository repo;

    public NotificationWorker(NotificationRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void processNotification(NotificationMessage message) {
        logger.info("Получено новое сообщение: {}", message);

        try {

            // Имитация отправки push-уведомления
            Thread.sleep(1000);
            boolean result = sendPush(message.getUserId(), message.getMessage());

            Notification notification = repo.findById(message.getId()).orElseThrow();

            if (result) {
                notification.setStatus(NotificationStatus.SENT);
            } else {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage("Push-сервис вернул ошибку");
            }
            notification.setUpdatedAt(LocalDateTime.now());
            repo.save(notification);
            logger.info("Уведомление {} обработано, статус {}", notification.getId(), notification.getStatus());
        } catch (Exception e) {
            logger.error("Ошибка при обработке сообщения {}", message.getId(), e);

            repo.findById(message.getId()).ifPresent(notification -> {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage(e.getMessage());
                notification.setUpdatedAt(LocalDateTime.now());
                repo.save(notification);
            });
        }
    }

    // Заглушка
    private boolean sendPush(String userId, String message) {
        logger.info("Отправка push-уведомления пользователю {}: {}", userId, message);
        return true;
    }
}
