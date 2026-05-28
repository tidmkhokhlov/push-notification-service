# 📬 Push Notification Service

Сервис для асинхронной отправки push-уведомлений с использованием RabbitMQ, аудитом в PostgreSQL, защитой через JWT и полной контейнеризацией.

---

## 🚀 Технологии

- **Java 17**
- **Spring Boot 3.5.14**
- **Spring Security + JWT**
- **Spring Data JPA (Hibernate)**
- **PostgreSQL 16**
- **RabbitMQ 3.13**
- **Docker / Docker Compose**
- **Maven**
- **OpenAPI (Swagger UI)**

---

## ✨ Функциональность

- ✅ REST API для отправки уведомлений (`POST /api/notifications`)
- ✅ Получение статуса уведомления по ID (`GET /api/notifications/{id}`)
- ✅ Асинхронная обработка через RabbitMQ
- ✅ Аудит всех операций в PostgreSQL (статусы, ошибки, количество попыток, временные метки)
- ✅ Гарантия доставки: повторные попытки (retry) с лимитом 3
- ✅ Защита всех эндпоинтов (кроме `/auth/login`) с помощью JWT
- ✅ Автоматическая документация OpenAPI (Swagger UI)
- ✅ Контейнеризация (Docker Compose поднимает всё приложение + БД + брокер)
- ✅ Простой фронтенд (одна HTML-страница для тестирования)

---

## 📁 Структура проекта

```
push-notification-service/
├── src/
│ ├── main/
│ │ ├── java/com/notification/push_service/
│ │ │ ├── config/
│ │ │ │ ├── RabbitMQConfig.java # настройка очередей, обменников, бинов для RabbitMQ
│ │ │ │ └── SecurityConfig.java # конфигурация Spring Security (JWT, открытые эндпоинты)
│ │ │ ├── controller/
│ │ │ │ ├── AuthController.java # эндпоинт /auth/login для выдачи JWT
│ │ │ │ └── NotificationController.java # REST API (создание, получение статуса)
│ │ │ ├── dto/
│ │ │ │ ├── NotificationMessage.java # DTO для передачи сообщения в RabbitMQ
│ │ │ │ ├── NotificationResponse.java # DTO для ответа клиенту
│ │ │ │ └── SendNotificationRequest.java # DTO для входящего запроса
│ │ │ ├── model/
│ │ │ │ ├── Notification.java # JPA сущность (таблица notifications)
│ │ │ │ └── NotificationStatus.java # Enum статусов (PENDING, SENT, FAILED)
│ │ │ ├── repository/
│ │ │ │ └── NotificationRepository.java # JPA репозиторий для работы с БД
│ │ │ ├── security/
│ │ │ │ ├── CustomUserDetailsService.java # загрузка пользователя для аутентификации
│ │ │ │ ├── JwtAuthenticationFilter.java # фильтр проверки JWT токена
│ │ │ │ └── JwtService.java # генерация, валидация JWT
│ │ │ ├── service/
│ │ │ │ └── NotificationWorker.java # RabbitMQ listener (асинхронная обработка, retry)
│ │ │ └── PushServiceApplication.java # точка входа Spring Boot
│ │ └── resources/
│ │ ├── static/
│ │ │ └── index.html # упрощённый фронтенд (тестирование API)
│ │ └── application.properties # конфигурация БД, JWT, RabbitMQ, JPA
│ └── test/... # тесты (в разработке)
├── .dockerignore # файлы, исключаемые из Docker-образа
├── .gitignore # файлы, исключаемые из Git
├── docker-compose.yml # оркестрация контейнеров (PostgreSQL, RabbitMQ, приложение)
├── Dockerfile # инструкция для сборки Docker-образа приложения
├── pom.xml # зависимости и сборка Maven
└── README.md # документация проекта
```

---

## 🧪 Как запустить

### Требования

- Docker и Docker Compose
- (опционально) Java 17+ и Maven – если запускать без Docker

### Клонировать репозиторий

```bash
git clone https://github.com/yourusername/push-notification-service.git
cd push-notification-service
```

### Запуск через Docker Compose (рекомендуемый способ)

```bash
docker-compose build
docker-compose up -d
```

Команда поднимет:
- PostgreSQL (порт 5432)
- RabbitMQ + Management UI (порты 5672 и 15672)
- Само приложение (порт 8080)

Приложение будет доступно по адресу: http://localhost:8080

### Локальный запуск (без Docker)

```bash
# Убедитесь, что PostgreSQL и RabbitMQ запущены
# Настройте application.properties под свои параметры

./mvnw spring-boot:run
```

---

## 🔑 Авторизация и получение JWT

Все API (кроме `/auth/login`) требуют Bearer токен.

### Получить токен:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

Ответ: `{"token":"eyJhbGciOiJIUzM4NCJ9..."}`.

Токен нужно передавать в заголовке:

```text
Authorization: Bearer <токен>
```

## 📡 API Endpoints

| Метод                                      | Эндпоинт                | Описание                                    |
|--------------------------------------------|-------------------------|---------------------------------------------|
| POST                                       | /api/notifications      | Создать уведомление (требуется JWT)         |
| GET                                        | /api/notifications/{id} | Получить статус уведомления (требуется JWT) |
| POST | /auth/login             | Получить JWT (публичный)                    |

### Пример запроса на отправку уведомления

```bash
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ваш_токен>" \
  -d '{"userId": "user123", "message": "Привет!"}'
```

Ответ:

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "userId": "user123",
  "message": "Привет!",
  "status": "PENDING",
  "createdAt": "2026-05-28T12:00:00",
  "updatedAt": "2026-05-28T12:00:00",
  "retryCount": 0,
  "errorMessage": null
}
```

---

## 🖥️ Фронтенд (упрощённый)

После запуска приложения откройте в браузере:
http://localhost:8080/index.html

Вы сможете:
- Залогиниться (admin/password)
- Отправить уведомление
- Проверить статус по ID

---

## 📄 Документация OpenAPI (Swagger)

Swagger UI доступен по адресу:
http://localhost:8080/swagger-ui.html
