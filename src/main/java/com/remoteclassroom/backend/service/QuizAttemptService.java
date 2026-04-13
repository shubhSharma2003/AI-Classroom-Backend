package com.remoteclassroom.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remoteclassroom.backend.dto.QuizAttemptRequest;
import com.remoteclassroom.backend.dto.QuizAttemptResponse;
import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.QuizAttempt;
import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.model.WeakTopic;
import com.remoteclassroom.backend.repository.QuizAttemptRepository;
import com.remoteclassroom.backend.repository.QuizRepository;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.repository.WeakTopicRepository;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository attemptRepository;

    @Autowired
    private WeakTopicRepository weakTopicRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public QuizAttemptResponse submitQuiz(String email, QuizAttemptRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Map<String, Object>> questions = parseQuestions(quiz.getQuestionsJson());

        int score = 0;
        List<String> weakTopicsList = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {

            Map<String, Object> q = questions.get(i);

            String correct = String.valueOf(q.get("correctAnswer"));
            String topic = String.valueOf(q.get("topic"));

            String selected = request.getAnswers().get(i);

            if (selected != null && correct.equals(selected)) {
                score++;
            } else {
                weakTopicsList.add(topic);
            }
        }

        weakTopicsList = weakTopicsList.stream().distinct().toList();

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(user);
        attempt.setQuiz(quiz);
        attempt.setAnswersJson(toJson(request.getAnswers()));
        attempt.setScore(score);

        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        for (String topic : weakTopicsList) {
            if (topic == null || topic.isBlank()) continue;

            WeakTopic wt = new WeakTopic();
            wt.setQuizAttempt(savedAttempt);
            wt.setTopic(topic.trim());
            wt.setCount(1);

            weakTopicRepository.save(wt);
        }

        return new QuizAttemptResponse(score, questions.size(), weakTopicsList);
    }

    private List<Map<String, Object>> parseQuestions(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error parsing questions JSON", e);
        }
    }

    private String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion error", e);
        }
    }
}