package com.lms.fullstack.repository;

import com.lms.fullstack.model.Course;
import com.lms.fullstack.model.Enrollment;
import com.lms.fullstack.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByCourse(Course course);

    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    long countByCourse(Course course);
}
