# Cinema Microservice System 🎬

## О проекте

Pet Project — распределенная микросервисная система для бронирования билетов в кино. Система построена на основе микросервисной архитектуры с использованием Spring Boot, Spring Cloud и Eureka для сервис-дискавери.

### Текущая архитектура (реализовано)

- **🎥 Movie Service**: Управление фильмами и сеансами
- **👥 User Service**: Управление пользователями
- **🎫 Booking Service**: Бронирование мест на сеансы
- **🔧 Config Server**: Централизованное управление конфигурациями
- **⚡ Eureka Server**: Сервис-дискавери для взаимодействия между микросервисами

### Технологический стек

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Cloud** (Config Server, Eureka Discovery)
- **Maven** (многомодульный проект)
- **Docker** (контейнеризация)
- **REST API**

## 🚀 Запуск проекта

### Предварительные требования

- JDK 17 или выше
- Maven 3.6+
- Docker (опционально)

### Локальный запуск

1. Клонируйте репозиторий:
git clone https://github.com/your-username/cinema.git

cd cinema

Movie Service 🎥
GET /api/movies - получить список всех фильмов

GET /api/movies/{id} - получить информацию о фильме по ID

GET /api/movies/search/title?title={title} - поиск фильмов по названию

GET /api/movies/search/genre?genre={genre} - поиск фильмов по жанру

POST /api/movies - создать новый фильм

PUT /api/movies/{id} - обновить информацию о фильме

DELETE /api/movies/{id} - удалить фильм

User Service 👥
GET /api/users - получить список всех пользователей

GET /api/users/{id} - получить информацию о пользователе по ID

POST /api/users - создать нового пользователя

PUT /api/users/{id} - обновить информацию о пользователе

DELETE /api/users/{id} - удалить пользователя

Booking Service 🎫
GET /bookings - получить список всех бронирований

GET /bookings/{id} - получить информацию о бронировании по ID

GET /bookings/user/{userId} - получить бронирования конкретного пользователя

POST /bookings - создать новое бронирование

DELETE /bookings/{bookingId} - отменить бронирование


🔮 Планы по развитию (In Progress 🚧)
Проект находится в активной разработке. Планируется добавить:

API Gateway (Spring Cloud Gateway) - единая точка входа

Аутентификация и авторизация (Spring Security + JWT)

Асинхронная коммуникация (Apache Kafka)
