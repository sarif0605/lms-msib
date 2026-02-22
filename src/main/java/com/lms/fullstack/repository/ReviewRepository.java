package com.lms.fullstack.repository;

import com.lms.fullstack.model.Course;
import com.lms.fullstack.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourse(Course course);
}
