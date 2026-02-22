package com.lms.fullstack.repository;

import com.lms.fullstack.model.Chapter;
import com.lms.fullstack.model.User;
import com.lms.fullstack.model.UserChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserChapterProgressRepository extends JpaRepository<UserChapterProgress, Long> {
    Optional<UserChapterProgress> findByUserAndChapter(User user, Chapter chapter);
    List<UserChapterProgress> findByUserAndChapter_Course(User user, com.lms.fullstack.model.Course course);
}
