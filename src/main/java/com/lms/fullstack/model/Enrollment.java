package com.lms.fullstack.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime enrolledAt;

    private Double progressPercentage;

    private Boolean isCompleted;

    @PrePersist
    protected void onEnroll() {
        enrolledAt = LocalDateTime.now();
        progressPercentage = 0.0;
        isCompleted = false;
    }
}
