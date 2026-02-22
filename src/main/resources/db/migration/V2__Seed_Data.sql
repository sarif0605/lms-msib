-- Insert Demo Users
-- Note: Passwords are BCrypt hashes for 'password123'
-- Instructor
INSERT INTO users (email, password, full_name, bio, role) 
VALUES ('instructor@lmshub.com', '$2a$12$srgd7Jrg9MWi70O6WEwl3.ZwDBz.5/5wPFa9cfdOn8852NjZY04lm', 'Dr. Jane Smith', 'Senior Java Developer & Educator with 10 years of experience.', 'INSTRUCTOR');

-- Student 1
INSERT INTO users (email, password, full_name, bio, role) 
VALUES ('student1@gmail.com', '$2a$12$srgd7Jrg9MWi70O6WEwl3.ZwDBz.5/5wPFa9cfdOn8852NjZY04lm', 'Budi Santoso', 'Loves learning new backend technologies.', 'STUDENT');

-- Student 2
INSERT INTO users (email, password, full_name, bio, role) 
VALUES ('student2@gmail.com', '$2a$12$srgd7Jrg9MWi70O6WEwl3.ZwDBz.5/5wPFa9cfdOn8852NjZY04lm', 'Siti Aminah', 'Aspiring Fullstack Developer.', 'STUDENT');

-- Insert Demo Course
INSERT INTO courses (title, description, thumbnail, price, instructor_id, created_at)
VALUES ('Mastering Spring Boot 3 & Docker', 'Learn how to build and deploy modern Java applications using Spring Boot 3 and Docker containers.', 'https://img.freepik.com/free-vector/code-testing-concept-illustration_114360-1141.jpg', 49.99, 1, NOW());

-- Insert Demo Chapters
INSERT INTO chapters (course_id, title, content, video_url, order_index)
VALUES (1, 'Introduction to Spring Boot', 'In this chapter, we will learn the basics of Spring Boot 3 framework.', 'https://www.youtube.com/embed/gvfUuqS9z7c', 0);

INSERT INTO chapters (course_id, title, content, video_url, order_index)
VALUES (1, 'Dockerizing your Application', 'Learn how to create a Dockerfile and run your app in a container.', 'https://www.youtube.com/embed/3c-iBn73dDE', 1);
