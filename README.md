# Cinema Management System

Микросервисная система для управления кинотеатром.

## Архитектура проекта

Проект состоит из следующих микросервисов:

### 🛡️ Core Services
- **config-server** - централизованная конфигурация для всех микросервисов
- **eurekaserver** (Eureka Server) - регистрация и обнаружение сервисов
- **gatewayserver** - API Gateway с маршрутизацией и безопасностью

### 🎬 Business Services
- **movie** - управление фильмами, сеансами и расписанием
- **booking** - бронирование билетов и управление заказами
- **user** - управление пользователями и профилями

## Технологический стек

- **Java 17+** - основной язык разработки
- **Spring Boot 3.x** - фреймворк
- **Spring Cloud** - микросервисная архитектура
- **Keycloak** - OAuth2 и аутентификация
- **Spring Kafka** - интеграция с Apache Kafka
- **Eureka Server** - service discovery
- **Spring Cloud Gateway** - API Gateway
- **Spring Security** - безопасность
- **Maven/Gradle** - управление зависимостями
- **Docker** - контейнеризация

## Требования

- Java 17 и выше
- Maven 3.6+ и Gradle
- Docker и Docker Compose
- Keycloak 20+

## API Endpoints
# Gateway Routes

Аутентификация проходит через токен по адресу

POST /realms/cinema/protocol/openid-connect/token - получение access токена

GET /api/v1/movies/** - управление фильмами

POST /api/v1/bookings/** - бронирование билетов

GET /api/v1/users/** - управление пользователями

Movie Service
GET /movies - список фильмов

GET /movies/{id} - информация о фильме

POST /movies - добавление фильма (ADMIN)

PUT /movies/{id} - обновление фильма (ADMIN)

Booking Service
GET /bookings - список бронирований пользователя

POST /bookings - создание бронирования

DELETE /bookings/{id} - отмена бронирования

User Service
GET /users - информация о пользователях

PUT /users - обновление пользователя

GET /users - список пользователей (ADMIN)
