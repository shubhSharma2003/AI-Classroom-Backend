package com.remoteclassroom.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.StudentTopicMastery;
import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.QuizRepository;
import com.remoteclassroom.backend.repository.StudentTopicMasteryRepository;

@Service
public class AdaptiveQuizService {

    @Autowired
    private StudentTopicMasteryRepository studentTopicMasteryRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptService attemptService;

    @Autowired
    private AIService aiService;

    public Quiz generateAdaptiveQuiz(Long userId, Video video) {

        String difficulty = attemptService.getUserDifficultyAdvanced(userId);

        List<StudentTopicMastery> masteries =
                studentTopicMasteryRepository.findByStudent_IdOrderByMasteryLevelAsc(userId);

        String focusTopic = null;
        if (!masteries.isEmpty()) {
            // Get the topic with the lowest mastery level
            focusTopic = masteries.get(0).getTopicName();
        }

        String transcript = video.getTranscript();
        if (transcript == null || transcript.isBlank()) {
            throw new RuntimeException("Video has no transcript");
        }

        String questionsJson =
                aiService.generateQuiz(transcript, difficulty, focusTopic);

        Quiz quiz = new Quiz();
        quiz.setDifficulty(difficulty);
        quiz.setQuestionsJson(questionsJson);
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setVideo(video);

        return quizRepository.save(quiz);
    }
}