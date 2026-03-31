package com.remoteclassroom.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remoteclassroom.backend.dto.QuizAttemptRequest;
import com.remoteclassroom.backend.dto.QuizAttemptResponse;
import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.QuizAttempt;
import com.remoteclassroom.backend.model.WeakTopic;
import com.remoteclassroom.backend.repository.QuizAttemptRepository;
import com.remoteclassroom.backend.repository.QuizRepository;
import com.remoteclassroom.backend.repository.WeakTopicRepository;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository attemptRepository;

    @Autowired
    private WeakTopicRepository weakTopicRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    // =========================
    // 🚀 SUBMIT QUIZ
    // =========================
    @Transactional
    public QuizAttemptResponse submitQuiz(Long userId, QuizAttemptRequest request) {

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        int score = evaluateScore(
                quiz.getQuestionsJson(),
                request.getAnswersJson(),
                userId
        );

        try {
            QuizAttempt attempt = new QuizAttempt();
            attempt.setUserId(userId);
            attempt.setQuizId(request.getQuizId());

            String answersString = mapper.writeValueAsString(request.getAnswersJson());
            attempt.setAnswersJson(answersString);

            attempt.setScore(score);
            attempt.setAttemptedAt(LocalDateTime.now());

            attemptRepository.save(attempt);

        } catch (Exception e) {
            throw new RuntimeException("Error saving attempt", e);
        }

        QuizAttemptResponse response = new QuizAttemptResponse();
        response.setScore(score);
        response.setMessage("Quiz submitted successfully");

        return response;
    }


    private int evaluateScore(String questionsJson, List<Map<String, Object>> answers, Long userId) {

        try {
            List<Map<String, Object>> questions =
                    mapper.readValue(questionsJson, List.class);

            int score = 0;

            for (int i = 0; i < answers.size(); i++) {

                if (i >= questions.size()) break;

                String correct = String.valueOf(questions.get(i).get("correctAnswer"));
                String selected = String.valueOf(answers.get(i).get("selectedAnswer"));

                String topic = String.valueOf(questions.get(i).get("topic"));

                if (correct.equals(selected)) {
                    score++;
                } else {
                    saveWeakTopic(userId, topic);
                }
            }

            return (score * 100) / questions.size();

        } catch (Exception e) {
            throw new RuntimeException("Error evaluating quiz", e);
        }
    }

   
    @Transactional
   private void saveWeakTopic(Long userId, String topic) {

    if (topic == null) return;

    topic = topic.trim();

    List<WeakTopic> topics = weakTopicRepository.findAll();

    for (WeakTopic wt : topics) {
        if (wt.getUserId().equals(userId) &&
            wt.getTopic().trim().equalsIgnoreCase(topic)) {

            wt.setCount(wt.getCount() + 1);
            weakTopicRepository.save(wt);
            return;
        }
    }

    WeakTopic weak = new WeakTopic();
    weak.setUserId(userId);
    weak.setTopic(topic);
    weak.setCount(1);

    weakTopicRepository.save(weak);
}

    
    public String getUserDifficultyAdvanced(Long userId) {

        List<QuizAttempt> attempts = attemptRepository.findByUserId(userId);

        if (attempts.isEmpty()) return "EASY";

        List<QuizAttempt> recent = attempts.stream()
                .sorted((a, b) -> b.getAttemptedAt().compareTo(a.getAttemptedAt()))
                .limit(5)
                .toList();

        double avg = recent.stream()
                .mapToInt(QuizAttempt::getScore)
                .average()
                .orElse(0);

        if (avg >= 75) return "HARD";
        else if (avg >= 40) return "MEDIUM";
        else return "EASY";
    }
}