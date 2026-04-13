package com.remoteclassroom.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.WeakTopic;
import com.remoteclassroom.backend.repository.QuizRepository;
import com.remoteclassroom.backend.repository.WeakTopicRepository;

@Service
public class AdaptiveQuizService {

    @Autowired
    private WeakTopicRepository weakTopicRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptService attemptService;

    @Autowired
    private AIService aiService;

    public Quiz generateAdaptiveQuiz(Long userId, String transcript) {

        String difficulty = attemptService.getUserDifficultyAdvanced(userId);

        List<WeakTopic> weakTopics =
                weakTopicRepository.findByQuizAttempt_Student_IdOrderByCountDesc(userId);

        String focusTopic = null;
        if (!weakTopics.isEmpty()) {
            focusTopic = weakTopics.get(0).getTopic();
        }

        String questionsJson =
                aiService.generateQuiz(transcript, difficulty, focusTopic);

        Quiz quiz = new Quiz();
        quiz.setDifficulty(difficulty);
        quiz.setQuestionsJson(questionsJson);
        quiz.setCreatedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }
}