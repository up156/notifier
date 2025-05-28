# Notifier

Сервис на Spring Boot для оповещений пользователей с использованием cron и WebSocket.

## Возможности

- Хранение данных в PostgreSQL
- Приложение полностью настроено и запускается одной командой (`docker-compose up --build`)
- Данные и структура базы автоматически подготавливаются с помощью Liquibase
- Индивидуальные периоды информирования пользователей
- Оповещения по событиям (мгновенно или по расписанию)
- Периодическая проверка новых уведомлений (cron, раз в минуту)
- Уведомления в реальном времени через WebSocket
- REST API для работы с событиями и уведомлениями
- Swagger UI для интерактивной документации API

## Стек технологий

- Java 17
- Spring Boot
- Spring Data JPA
- Liquibase
- PostgreSQL
- WebSocket (Spring)
- OpenApi
- Docker

## Быстрый старт

Клонируйте репозиторий  
git clone https://github.com/up156/notifier.git  
mvn clean package  
docker-compose up --build  
Swagger:  
После запуска перейдите по адресу:  
http://localhost:8080/swagger-ui.html  
