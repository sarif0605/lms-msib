package com.lms.fullstack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_chapter_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChapterProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private Boolean isCompleted;
}
