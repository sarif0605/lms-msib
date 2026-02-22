package com.lms.fullstack.controller;

import com.lms.fullstack.model.Course;
import com.lms.fullstack.model.Review;
import com.lms.fullstack.model.User;
import com.lms.fullstack.repository.CourseRepository;
import com.lms.fullstack.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;

    @PostMapping("/courses/{courseId}/reviews")
    public String addReview(@PathVariable Long courseId,
            @AuthenticationPrincipal User user,
            @RequestParam Integer rating,
            @RequestParam String comment) {

        Course course = courseRepository.findById(courseId).orElseThrow();
        Review review = Review.builder()
                .course(course)
                .user(user)
                .rating(rating)
                .comment(comment)
                .build();

        reviewRepository.save(review);
        return "redirect:/courses/" + courseId;
    }
}
