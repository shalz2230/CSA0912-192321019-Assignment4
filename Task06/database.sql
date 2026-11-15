
CREATE DATABASE IF NOT EXISTS campus_events;

USE campus_events;

CREATE TABLE IF NOT EXISTS registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT unique_student_event
    UNIQUE (student_id, event_name)
);

INSERT INTO registrations
(student_id, student_name, event_name)
VALUES
('STU101', 'Arun Kumar', 'Tech Fest'),
('STU102', 'Priya', 'Cultural Fest'),
('STU103', 'Rahul', 'Coding Contest');
