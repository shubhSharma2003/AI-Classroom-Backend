package com.remoteclassroom.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.QuizRepository;
import com.remoteclassroom.backend.repository.VideoRepository;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private AIService aiService;

    public Quiz getOrGenerateQuiz(Long videoId, String studentEmail) {

        try {
            // 1️⃣ Get video
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));

            // 2️⃣ Get transcript
            String transcript = video.getTranscript();

            if (transcript == null || transcript.isBlank()) {
                throw new RuntimeException("Transcript not available");
            }

            // 3️⃣ Adaptive (disabled for now)
            String weakTopic = null;

            // 4️⃣ Generate quiz from AI
            String questionsJson = aiService.generateQuiz(transcript, "MEDIUM", weakTopic);

            // 5️⃣ Save quiz
            Quiz quiz = new Quiz();
            quiz.setVideo(video);
            quiz.setTeacher(video.getTeacher());
            quiz.setQuestionsJson(questionsJson);
            quiz.setDifficulty("MEDIUM");
            quiz.setTotalQuestions(10);

            return quizRepository.save(quiz);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Quiz generation failed");
        }
    }
}