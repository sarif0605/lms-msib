package com.lms.fullstack.controller;

import com.lms.fullstack.model.Chapter;
import com.lms.fullstack.model.Course;
import com.lms.fullstack.model.User;
import com.lms.fullstack.service.CourseService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/courses/{id}")
    public String viewCourse(@PathVariable Long id, Model model, @AuthenticationPrincipal User user) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);

        boolean isEnrolled = false;
        boolean isCompleted = false;
        if (user != null) {
            isEnrolled = courseService.isUserEnrolled(user, course);
            if (isEnrolled) {
                isCompleted = courseService.isCourseCompleted(user, course);
            }
            if (course.getInstructor().getId().equals(user.getId())) {
                isEnrolled = true;
            }
        }
        model.addAttribute("isEnrolled", isEnrolled);
        model.addAttribute("isCompleted", isCompleted);
        model.addAttribute("currentUser", user);

        return "course/detail";
    }

    @PostMapping("/courses/{courseId}/enroll")
    public String enroll(@PathVariable Long courseId, @AuthenticationPrincipal User user) {
        courseService.enroll(user, courseId);
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/chapters/{chapterId}/complete")
    public String completeChapter(@PathVariable Long chapterId, @AuthenticationPrincipal User user) {
        courseService.completeChapter(user, chapterId);
        Chapter chapter = courseService.getChapterById(chapterId);
        return "redirect:/courses/" + chapter.getCourse().getId() + "/learn";
    }

    @GetMapping("/courses/{id}/learn")
    public String learnCourse(@PathVariable Long id,
            @PathVariable(required = false) Long chapterId,
            Model model,
            @AuthenticationPrincipal User user) {
        Course course = courseService.getCourseById(id);

        // Security check
        if (user == null || !courseService.isUserEnrolled(user, course)) {
            return "redirect:/courses/" + id;
        }

        model.addAttribute("course", course);

        // Find current chapter
        Chapter currentChapter = null;
        if (chapterId != null) {
            currentChapter = courseService.getChapterById(chapterId);
        } else {
            // Find first incomplete chapter
            currentChapter = courseService.getFirstIncompleteChapter(user, course);
        }

        model.addAttribute("currentChapter", currentChapter);
        model.addAttribute("isCompleted", courseService.isCourseCompleted(user, course));
        model.addAttribute("enrollment", courseService.getEnrollment(user, course));
        model.addAttribute("chapterProgress", courseService.getChapterProgress(user, course));

        return "course/learn";
    }

    @PostMapping("/courses/{id}/reviews")
    public String addReview(@PathVariable Long id,
            @RequestParam Integer rating,
            @RequestParam String comment,
            @AuthenticationPrincipal User user) {
        courseService.addReview(user, id, rating, comment);
        return "redirect:/courses/" + id;
    }
}
