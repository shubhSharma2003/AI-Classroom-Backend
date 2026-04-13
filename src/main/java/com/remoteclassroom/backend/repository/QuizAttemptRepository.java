package com.remoteclassroom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.remoteclassroom.backend.model.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // ✅ FIX 1: user → student
    List<QuizAttempt> findByStudent_Id(Long userId);

    // ✅ FIX 2: user + quizId → student + quiz.id
    List<QuizAttempt> findByStudent_IdAndQuiz_Id(Long userId, Long quizId);

    // ✅ FIX 3: videoId → quiz.video.id
    List<QuizAttempt> findByQuiz_Video_Id(Long videoId);
}