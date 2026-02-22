package com.lms.fullstack.controller;

import com.lms.fullstack.model.Chapter;
import com.lms.fullstack.model.Course;
import com.lms.fullstack.model.User;
import com.lms.fullstack.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InstructorController {

    private final CourseRepository courseRepository;

    @GetMapping("/instructor/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("isEdit", false);
        return "instructor/upload-course";
    }

    @GetMapping("/instructor/courses/{id}/edit")
    public String editCourseForm(@PathVariable Long id, org.springframework.ui.Model model,
            @AuthenticationPrincipal User instructor) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("course", course);
        model.addAttribute("isEdit", true);
        return "instructor/upload-course";
    }

    @PostMapping("/instructor/courses")
    public String createCourse(@AuthenticationPrincipal User instructor,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam(required = false) String thumbnail,
            @RequestParam List<String> chapterTitles,
            @RequestParam List<String> chapterVideos,
            @RequestParam List<String> chapterContents,
            @RequestParam(required = false) List<String> chapterAssignments,
            @RequestParam(required = false) List<String> chapterQuizzes) {

        Course course = Course.builder()
                .title(title)
                .description(description)
                .price(price)
                .thumbnail(thumbnail)
                .instructor(instructor)
                .build();

        saveCourseWithChapters(course, chapterTitles, chapterVideos, chapterContents, chapterAssignments,
                chapterQuizzes);
        return "redirect:/dashboard";
    }

    @PostMapping("/instructor/courses/{id}")
    public String updateCourse(@PathVariable Long id, @AuthenticationPrincipal User instructor,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam(required = false) String thumbnail,
            @RequestParam List<String> chapterTitles,
            @RequestParam List<String> chapterVideos,
            @RequestParam List<String> chapterContents,
            @RequestParam(required = false) List<String> chapterAssignments,
            @RequestParam(required = false) List<String> chapterQuizzes) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            return "redirect:/dashboard";
        }

        course.setTitle(title);
        course.setDescription(description);
        course.setPrice(price);
        course.setThumbnail(thumbnail);

        saveCourseWithChapters(course, chapterTitles, chapterVideos, chapterContents, chapterAssignments,
                chapterQuizzes);
        return "redirect:/dashboard";
    }

    private void saveCourseWithChapters(Course course, List<String> chapterTitles, List<String> chapterVideos,
            List<String> chapterContents, List<String> chapterAssignments, List<String> chapterQuizzes) {

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        List<Chapter> chapters = new ArrayList<>();
        for (int i = 0; i < chapterTitles.size(); i++) {
            Chapter chapter = Chapter.builder()
                    .course(course)
                    .title(chapterTitles.get(i))
                    .videoUrl(chapterVideos.get(i))
                    .content(chapterContents.get(i))
                    .assignment(chapterAssignments != null && i < chapterAssignments.size() ? chapterAssignments.get(i)
                            : null)
                    .orderIndex(i)
                    .build();

            if (chapterQuizzes != null && i < chapterQuizzes.size() && !chapterQuizzes.get(i).isEmpty()) {
                try {
                    String quizJson = chapterQuizzes.get(i);
                    com.lms.fullstack.model.Quiz quiz = mapper.readValue(quizJson, com.lms.fullstack.model.Quiz.class);
                    quiz.setChapter(chapter);
                    if (quiz.getQuestions() != null) {
                        for (com.lms.fullstack.model.Question q : quiz.getQuestions()) {
                            q.setQuiz(quiz);
                            if (q.getChoices() != null) {
                                for (com.lms.fullstack.model.Choice c : q.getChoices()) {
                                    c.setQuestion(q);
                                }
                            }
                        }
                    }
                    chapter.setQuiz(quiz);
                } catch (Exception e) {
                    // Log error or skip quiz
                }
            }
            chapters.add(chapter);
        }

        if (course.getChapters() != null) {
            course.getChapters().clear();
            course.getChapters().addAll(chapters);
        } else {
            course.setChapters(chapters);
        }

        courseRepository.save(course);
    }
}
