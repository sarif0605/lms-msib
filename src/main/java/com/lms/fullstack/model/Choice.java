package com.lms.fullstack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "choices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false)
    private String text;

    private Boolean isCorrect;
}
