# Notifier

Сервис на Spring Boot для оповещений пользователей с использованием cron и WebSocket.

## Возможности

- Хранение данных в PostgreSQL
- Приложение полностью настроено и запускается одной командой (`docker-compose up --build`)
- Автоматическая подготовка структуры и данных БД через Liquibase
- Индивидуальные периоды информирования пользователей
- Оповещения по событиям (мгновенно или по расписанию)
- Периодическая проверка новых уведомлений (cron, раз в минуту)
- Push-Уведомления в реальном времени через WebSocket
- REST API для работы с событиями и уведомлениями
- Swagger UI для интерактивной документации API
- Unit-тесты и тестирование API (Testcontainters)

## Стек технологий

- Java 17
- Spring Boot
- Spring Data JPA
- Liquibase
- PostgreSQL
- WebSocket (Spring)
- OpenApi (Swagger)
- Docker
- JUnit
- Testcontainers

## Быстрый старт

1. Клонируйте репозиторий  
git clone https://github.com/up156/notifier.git  
2. mvn clean package  
3. docker-compose up --build  
4. Swagger:  
После запуска перейдите по адресу:  
http://localhost:8080/swagger-ui.html  
5. Postman-коллекция (`notifier.postman_collection.json`) лежит в корне проекта. Импортируйте её в Postman для быстрого запуска и тестирования сценариев.