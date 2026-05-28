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
    public void processNotification(NotificationMessage message) {
        logger.info("Получено новое сообщение: {}", message);

        Notification notification = repo.findById(message.getId()).orElseThrow();
        int currentRetry = notification.getRetryCount() == null ? 0 : notification.getRetryCount();

        try {

            // Имитация отправки push-уведомления
            Thread.sleep(1000);
            boolean result = sendPush(message.getUserId(), message.getMessage());

            if (result) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setRetryCount(0);
                notification.setErrorMessage(null);
                notification.setUpdatedAt(LocalDateTime.now());
                repo.save(notification);
                logger.info("Уведомление {} успешно отправлено", notification.getId());
            } else {
                throw new RuntimeException("Push-сервис вернул ошибку");
            }
        } catch (Exception e) {
            logger.error("Ошибка при обработке сообщения {}, попытка {}", notification.getId(), currentRetry+1, e);

            int newRetry = currentRetry + 1;
            notification.setRetryCount(newRetry);
            notification.setErrorMessage(e.getMessage());

            if (newRetry >= 3) {
                notification.setStatus(NotificationStatus.FAILED);
                notification.setUpdatedAt(LocalDateTime.now());
                repo.save(notification);
                logger.error("Уведомление {} окончательно не удалось обработать после {} попыток", notification.getId(), notification.getRetryCount());
            } else {
                notification.setStatus(NotificationStatus.PENDING);
                notification.setUpdatedAt(LocalDateTime.now());
                repo.save(notification);
                throw new RuntimeException("Уведомление" + notification.getId() + " снова не удалось обработать", e);
            }
        }
    }

    // Заглушка
    private boolean sendPush(String userId, String message) {
        logger.info("Отправка push-уведомления пользователю {}: {}", userId, message);
        return true;
    }
}
