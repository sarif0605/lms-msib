package com.lms.fullstack.controller;

import com.lms.fullstack.model.Quiz;
import com.lms.fullstack.model.Question;
import com.lms.fullstack.model.Choice;
import com.lms.fullstack.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class QuizController {

    private final QuizRepository quizRepository;

    @GetMapping("/quizzes/{id}")
    public String takeQuiz(@PathVariable Long id, Model model) {
        Quiz quiz = quizRepository.findById(id).orElseThrow();
        model.addAttribute("quiz", quiz);
        return "quiz/take";
    }

    @PostMapping("/quizzes/{id}/submit")
    public String submitQuiz(@PathVariable Long id, @RequestParam Map<String, String> answers, Model model) {
        Quiz quiz = quizRepository.findById(id).orElseThrow();
        int totalQuestions = quiz.getQuestions().size();
        int correctAnswers = 0;

        for (Question question : quiz.getQuestions()) {
            String submittedChoiceId = answers.get("question_" + question.getId());
            if (submittedChoiceId != null) {
                Long choiceId = Long.parseLong(submittedChoiceId);
                boolean isCorrect = question.getChoices().stream()
                        .filter(Choice::getIsCorrect)
                        .anyMatch(c -> c.getId().equals(choiceId));
                if (isCorrect) {
                    correctAnswers++;
                }
            }
        }

        double score = (double) correctAnswers / totalQuestions * 100;
        model.addAttribute("score", score);
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("quiz", quiz);

        return "quiz/result";
    }
}
