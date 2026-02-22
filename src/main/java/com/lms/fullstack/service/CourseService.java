package com.lms.fullstack.service;

import com.lms.fullstack.model.*;
import com.lms.fullstack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserChapterProgressRepository progressRepository;
    private final ReviewRepository reviewRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow();
    }

    public Chapter getChapterById(Long id) {
        return chapterRepository.findById(id).orElseThrow();
    }

    public boolean isUserEnrolled(User user, Course course) {
        return enrollmentRepository.findByStudentAndCourse(user, course).isPresent();
    }

    public boolean isCourseCompleted(User user, Course course) {
        return enrollmentRepository.findByStudentAndCourse(user, course)
                .map(Enrollment::getIsCompleted)
                .orElse(false);
    }

    public Enrollment getEnrollment(User user, Course course) {
        return enrollmentRepository.findByStudentAndCourse(user, course).orElse(null);
    }

    @Transactional
    public void enroll(User student, Long courseId) {
        Course course = getCourseById(courseId);
        if (enrollmentRepository.findByStudentAndCourse(student, course).isEmpty()) {
            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .course(course)
                    .build();
            enrollmentRepository.save(enrollment);

            // Initialize progress for all chapters
            course.getChapters().forEach(chapter -> {
                UserChapterProgress progress = UserChapterProgress.builder()
                        .user(student)
                        .chapter(chapter)
                        .isCompleted(false)
                        .build();
                progressRepository.save(progress);
            });
        }
    }

    @Transactional
    public void completeChapter(User student, Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId).orElseThrow();
        UserChapterProgress progress = progressRepository.findByUserAndChapter(student, chapter)
                .orElseGet(() -> UserChapterProgress.builder()
                        .user(student)
                        .chapter(chapter)
                        .isCompleted(false)
                        .build());

        if (!progress.getIsCompleted()) {
            progress.setIsCompleted(true);
            progressRepository.save(progress);

            updateProgressPercentage(student, chapter.getCourse());
        }
    }

    private void updateProgressPercentage(User student, Course course) {
        List<UserChapterProgress> progressList = progressRepository.findByUserAndChapter_Course(student, course);
        long completed = progressList.stream().filter(UserChapterProgress::getIsCompleted).count();
        double percentage = (double) completed / progressList.size() * 100;

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course).orElseThrow();
        enrollment.setProgressPercentage(percentage);
        if (percentage >= 100) {
            enrollment.setIsCompleted(true);
        }
        enrollmentRepository.save(enrollment);
    }

    public Chapter getFirstIncompleteChapter(User user, Course course) {
        List<UserChapterProgress> progressList = progressRepository.findByUserAndChapter_Course(user, course);
        return course.getChapters().stream()
                .filter(chapter -> progressList.stream()
                        .anyMatch(p -> p.getChapter().getId().equals(chapter.getId()) && !p.getIsCompleted()))
                .findFirst()
                .orElse(course.getChapters().isEmpty() ? null : course.getChapters().get(0));
    }

    @Transactional
    public void addReview(User user, Long courseId, Integer rating, String comment) {
        Course course = getCourseById(courseId);
        Review review = Review.builder()
                .user(user)
                .course(course)
                .rating(rating)
                .comment(comment)
                .build();
        reviewRepository.save(review);
    }

    public List<UserChapterProgress> getChapterProgress(User user, Course course) {
        return progressRepository.findByUserAndChapter_Course(user, course);
    }
}
