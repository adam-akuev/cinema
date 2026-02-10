# 🎬 Cinema Management System

<div align="center">

![Microservices](https://img.shields.io/badge/Architecture-Microservices-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Java](https://img.shields.io/badge/Java-17+-orange)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)

**Полнофункциональная система управления кинотеатром с микросервисной архитектурой**

[Архитектура](#-архитектура) • [Функциональности](#-функциональности) • [Запуск](#-запуск) • [API](#-api-документация)

</div>

## 🌟 О проекте

Современная система управления кинотеатром, построенная на микросервисной архитектуре. Обеспечивает бронирование билетов, управление фильмами и пользователями с высокой доступностью и масштабируемостью.

## 🏗️ Архитектура

## 🛠️ Технологический стек
Backend Services
Java 17+ + Spring Boot 3.x + Spring Cloud

Spring Security + Keycloak (OAuth2/OIDC)

Spring Data JPA + PostgreSQL (основная БД)

Redis (кэширование)

Apache Kafka (асинхронная коммуникация)

Infrastructure
Docker + Docker Compose (контейнеризация)

Eureka (service discovery)

Spring Cloud Gateway (API gateway)

Config Server (централизованная конфигурация)

Monitoring & Observability
ELK Stack (Elasticsearch, Logstash, Kibana) - логирование

Zipkin - распределенная трассировка

PgAdmin - управление БД

## 📋 Сервисы системы

### 🛡️ Core Services
| Сервис | Порт | Описание |
|--------|------|----------|
| **config-server** | 8888 | Централизованная конфигурация |
| **eureka-server** | 8761 | Service Discovery |
| **gateway-server** | 8084 | API Gateway |

### 🎬 Business Services
| Сервис | Порт | Описание |
|--------|------|----------|
| **movie-service** | 8081 | Управление фильмами и сеансами |
| **booking-service** | 8083 | Бронирование билетов |
| **user-service** | 8082 | Управление пользователями |

### 🗄️ Infrastructure
| Сервис | Порт | Описание |
|--------|------|----------|
| **PostgreSQL** | 5432 | Основная база данных |
| **Keycloak** | 8080 | Аутентификация и авторизация |
| **Redis** | 6379 | Кэширование |
| **Kafka** | 9092 | Асинхронная коммуникация |
| **ELK Stack** | 5601 | Логирование и мониторинг |
| **Zipkin** | 9411 | Распределенная трассировка |

## 🎯 Функциональности

# 🎥 Movie Service
📋 Управление каталогом фильмов

🎭 Создание и управление сеансами

📅 Расписание показов

👥 Информация о фильмах (режиссер, актеры, длительность)

# 🎫 Booking Service
💺 Бронирование билетов

🔄 Управление доступностью мест

📊 История бронирований

❌ Отмена бронирований

# 👥 User Service
👤 Регистрация и управление пользователями

🔐 Ролевая модель (USER, ADMIN)

## 🚀 Быстрый старт
Предварительные требования
Java 17 и выше

Maven 3.6+ или Gradle

Docker и Docker Compose

Keycloak 20+

# Шаг 1: Сборка проекта

Клонирование репозитория
```
git clone https://github.com/adam-akuev/cinema.git
cd cinema-management
```

Сборка отдельных сервисов
```
cd movie-service && mvn clean package
cd ../booking-service && mvn clean package
... и т.д.
```

# Шаг 2: Запуск через Docker Compose

Запуск всей инфраструктуры
```
docker-compose up -d
```

Проверка статуса всех сервисов
```
docker-compose ps
```

# Шаг 3: Получение токена доступа

Получение access token через Keycloak
```
POST http://localhost:8080/realms/cinema/protocol/openid-connect/token
```

# Шаг 4: Использование API
📡 API Документация
| Сервис | Swagger UI URL | OpenAPI JSON |
|--------|----------------|--------------|
| **Movie Service** | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| **Booking Service** | http://localhost:8083/swagger-ui.html | http://localhost:8083/v3/api-docs |
| **User Service** | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |

🔐 Аутентификация

POST /realms/cinema/protocol/openid-connect/token                     // Получение access token для доступа к API.

## 🌐 Доступ к сервисам
После запуска системы доступны следующие интерфейсы:

API Gateway	http://localhost:8084	Основной API

Eureka Dashboard	http://localhost:8761	Мониторинг сервисов

Keycloak Admin	http://localhost:8080	Управление аутентификацией

PgAdmin	http://localhost:5050	Управление БД

Kibana	http://localhost:5601	Визуализация логов

Zipkin	http://localhost:9411	Трассировка запросов

Учетные данные:

Keycloak: admin / admin

PgAdmin: admin@example.com / 0095

# 🔧 Разработка
Локальная разработка

# Запуск только инфраструктуры
```
docker-compose up -d postgres redis kafka zookeeper keycloak
```

📊 Data Flow
📱 Запрос от клиента → API Gateway (8084)

🔐 Аутентификация → Keycloak (8080)

🔍 Service Discovery → Eureka Server (8761)

🛣️ Маршрутизация → Соответствующий микросервис

💾 Обработка данных → PostgreSQL/Redis

⚡ Асинхронные задачи → Kafka

📝 Логирование → ELK Stack

👀 Мониторинг → Zipkin трассировка

👨‍💻 Автор
Акуев Адам

🙏 Благодарности
Spring Boot team за отличный фреймворк

Docker community за инструменты контейнеризации

Сообщество микросервисных архитектур за лучшие практики
