package com.lms.fullstack.controller;

import com.lms.fullstack.model.*;
import com.lms.fullstack.repository.EnrollmentRepository;
import com.lms.fullstack.repository.CourseRepository;
import com.lms.fullstack.repository.ReviewRepository;
import com.lms.fullstack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        if (user.getRole() == Role.ADMIN) {
            model.addAttribute("totalUsers", userRepository.count());
            model.addAttribute("totalCourses", courseRepository.count());
            model.addAttribute("totalEnrollments", enrollmentRepository.count());

            double totalRevenue = enrollmentRepository.findAll().stream()
                    .mapToDouble(e -> e.getCourse() != null ? e.getCourse().getPrice() : 0.0)
                    .sum();
            model.addAttribute("totalRevenue", String.format("%.2f", totalRevenue));

            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("allCourses", courseRepository.findAll());

            return "admin/dashboard";
        } else if (user.getRole() == Role.INSTRUCTOR) {
            List<Course> myCourses = courseRepository.findByInstructor(user);
            model.addAttribute("myCourses", myCourses);

            double avgRating = myCourses.stream()
                    .flatMap(c -> reviewRepository.findByCourse(c).stream())
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            List<Enrollment> instructorEnrollments = myCourses.stream()
                    .flatMap(c -> enrollmentRepository.findByCourse(c).stream())
                    .collect(Collectors.toList());

            model.addAttribute("enrollments", instructorEnrollments);
            model.addAttribute("totalCourses", myCourses.size());
            model.addAttribute("totalStudents", (long) instructorEnrollments.size());
            model.addAttribute("avgRating", String.format("%.1f", avgRating));

            return "instructor/dashboard";
        } else {
            List<Enrollment> enrollments = enrollmentRepository.findByStudent(user);
            model.addAttribute("enrollments", enrollments);

            double overallProgress = enrollments.stream()
                    .mapToDouble(Enrollment::getProgressPercentage)
                    .average()
                    .orElse(0.0);

            long completedCourses = enrollments.stream()
                    .filter(Enrollment::getIsCompleted)
                    .count();

            model.addAttribute("overallProgress", String.format("%.0f", overallProgress));
            model.addAttribute("completedCourses", completedCourses);
            model.addAttribute("activeCourses", enrollments.size() - completedCourses);

            return "student/dashboard";
        }
    }
}
