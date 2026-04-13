package com.remoteclassroom.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.UserRepository;
import com.remoteclassroom.backend.repository.VideoRepository;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    public Video saveVideo(String title, String url, String email, String transcript) {

        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Video video = new Video();
        video.setTitle(title);
        video.setUrl(url);
        video.setTeacher(teacher);
        video.setTranscript(transcript);
        video.setUploadedAt(LocalDateTime.now());

        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getMyVideos(String email) {
        return videoRepository.findByTeacher_Email(email);
    }

    // 🔥 NEW
    public Video getById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found"));
    }
}