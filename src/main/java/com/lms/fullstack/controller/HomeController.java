package com.lms.fullstack.controller;

import com.lms.fullstack.model.Role;
import com.lms.fullstack.model.User;
import com.lms.fullstack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.lms.fullstack.repository.CourseRepository courseRepository;
    private final com.lms.fullstack.repository.EnrollmentRepository enrollmentRepository;

    @GetMapping("/")
    public String index(org.springframework.ui.Model model,
            @org.springframework.security.core.annotation.AuthenticationPrincipal User user) {
        model.addAttribute("courses", courseRepository.findAll());
        if (user != null) {
            java.util.List<Long> enrolledCourseIds = enrollmentRepository.findByStudent(user).stream()
                    .map(e -> e.getCourse().getId())
                    .toList();
            model.addAttribute("enrolledCourseIds", enrolledCourseIds);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam Role role) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .build();
        userRepository.save(user);
        return "redirect:/login";
    }
}
