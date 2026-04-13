package com.remoteclassroom.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.remoteclassroom.backend.model.Quiz;
import com.remoteclassroom.backend.model.Video;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Quiz findByVideo(Video video);
}