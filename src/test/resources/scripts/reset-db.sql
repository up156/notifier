DELETE FROM notification;
DELETE FROM notification_period;
DELETE FROM event;
DELETE FROM users;

INSERT INTO users (id, name) VALUES (1, 'Иванов Иван Иванович');
INSERT INTO users (id, name) VALUES (2, 'Петров Петр Петрович');

INSERT INTO notification_period (id, day_of_week, time_from, time_to, user_id)
VALUES (1, 'MONDAY', '08:00:00', '12:00:00', 1);

INSERT INTO notification_period (id, day_of_week, time_from, time_to, user_id)
VALUES (2, 'FRIDAY', '09:00:00', '11:30:00', 1);

INSERT INTO notification_period (id, day_of_week, time_from, time_to, user_id)
VALUES (3, 'TUESDAY', '10:00:00', '12:00:00', 2);


INSERT INTO event (id, message, event_date_time)
VALUES (1, 'Event 1', '2025-05-28 15:00:00');

INSERT INTO event (id, message, event_date_time)
VALUES (2, 'Event 2', '2025-05-29 15:00:00');


INSERT INTO notification (id, user_id, event_id, status, scheduled_for, sent_at)
VALUES (1, 1, 1, 'SENT', '2025-05-30 09:05:00', '2025-05-30 09:06:00');

INSERT INTO notification (id, user_id, event_id, status, scheduled_for, sent_at)
VALUES (2, 2, 2, 'WAITING', '2025-05-31 14:30:00', NULL);


ALTER SEQUENCE event_id_seq RESTART WITH 3;
ALTER SEQUENCE notification_id_seq RESTART WITH 3;
ALTER SEQUENCE notification_period_id_seq RESTART WITH 4;
ALTER SEQUENCE users_id_seq RESTART WITH 3;