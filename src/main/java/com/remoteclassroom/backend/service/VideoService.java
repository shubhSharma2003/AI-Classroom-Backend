package com.remoteclassroom.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.VideoRepository;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    // ✅ Save video metadata
    public Video saveVideo(String title, String url, String uploadedBy) {
        Video video = new Video();
        video.setTitle(title);
        video.setUrl(url);
        video.setUploadedBy(uploadedBy);
        video.setUploadedAt(LocalDateTime.now());

        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getMyVideos(String username) {
        return videoRepository.findByUploadedBy(username);
    }
}