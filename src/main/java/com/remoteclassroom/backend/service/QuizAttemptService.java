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
import com.remoteclassroom.backend.repository.QuizAttemptRepository;
import com.remoteclassroom.backend.repository.QuizRepository;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.repository.StudentTopicMasteryRepository;
import com.remoteclassroom.backend.model.StudentTopicMastery;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class QuizAttemptService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository attemptRepository;
    
    @Autowired
    private StudentTopicMasteryRepository studentTopicMasteryRepository;

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
        Map<String, int[]> topicStats = new HashMap<>();

        for (int i = 0; i < questions.size(); i++) {

            Map<String, Object> q = questions.get(i);

            String correct = String.valueOf(q.get("correctAnswer"));
            String topic = String.valueOf(q.get("topic"));
            if (topic == null || topic.isBlank()) continue;
            topic = topic.trim();

            String selected = request.getAnswers().get(i);
            boolean isCorrect = selected != null && correct.equals(selected);

            if (isCorrect) {
                score++;
            } else {
                weakTopicsList.add(topic);
            }
            
            int[] stats = topicStats.getOrDefault(topic, new int[]{0, 0});
            stats[0]++; // attempted
            if (isCorrect) stats[1]++; // correct count
            topicStats.put(topic, stats);
        }

        weakTopicsList = weakTopicsList.stream().distinct().toList();

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudent(user);
        attempt.setQuiz(quiz);
        attempt.setAnswersJson(toJson(request.getAnswers()));
        attempt.setScore(score);

        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        // Update aggregated StudentTopicMastery
        for (Map.Entry<String, int[]> entry : topicStats.entrySet()) {
            String topicName = entry.getKey();
            int attempted = entry.getValue()[0];
            int correctCount = entry.getValue()[1];
            
            StudentTopicMastery mastery = studentTopicMasteryRepository
                    .findByStudent_IdAndTopicName(user.getId(), topicName)
                    .orElseGet(() -> {
                        StudentTopicMastery m = new StudentTopicMastery();
                        m.setStudent(user);
                        m.setTopicName(topicName);
                        return m;
                    });
            
            mastery.setTotalAttempted(mastery.getTotalAttempted() + attempted);
            mastery.setCorrectCount(mastery.getCorrectCount() + correctCount);
            mastery.setMasteryLevel(((double) mastery.getCorrectCount() / mastery.getTotalAttempted()) * 100);
            mastery.setLastAttemptDate(LocalDateTime.now());
            
            studentTopicMasteryRepository.save(mastery);
        }

        return new QuizAttemptResponse(score, questions.size(), weakTopicsList);
    }

    // ✅ NEW METHOD ADDED (FIX)
    public String getUserDifficultyAdvanced(Long userId) {

        List<QuizAttempt> attempts = attemptRepository.findByStudent_Id(userId);

        if (attempts.isEmpty()) {
            return "BEGINNER";
        }

        attempts.sort(Comparator.comparing(QuizAttempt::getId));

        double ema = 0.0;
        boolean first = true;
        for (QuizAttempt attempt : attempts) {
            double score = attempt.getScore();
            if (first) {
                ema = score;
                first = false;
            } else {
                // 70% weight to recent score, 30% to historical average
                ema = (0.7 * score) + (0.3 * ema);
            }
        }

        if (ema >= 8.0) return "ADVANCED";
        else if (ema >= 5.0) return "INTERMEDIATE";
        else return "BEGINNER";
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