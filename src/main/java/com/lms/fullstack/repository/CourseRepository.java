package com.lms.fullstack.repository;

import com.lms.fullstack.model.Course;
import com.lms.fullstack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructor(User instructor);
}
