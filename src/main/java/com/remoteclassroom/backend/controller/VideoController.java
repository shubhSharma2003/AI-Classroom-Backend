package com.remoteclassroom.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.service.S3Service;
import com.remoteclassroom.backend.service.VideoService;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            Authentication authentication
    ) {
        try {
            // Step 1: Upload file to S3
            String fileUrl = s3Service.uploadFile(file);

            // Step 2: Get logged-in user
            String username = authentication.getName();

            // Step 3: Save metadata in DB
            Video savedVideo = videoService.saveVideo(title, fileUrl, username);

            return ResponseEntity.ok(savedVideo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Video> getAllVideos() {
        return videoService.getAllVideos();
    }

    @GetMapping("/my")
    public List<Video> getMyVideos(Authentication authentication) {
        String username = authentication.getName();
        return videoService.getMyVideos(username);
    }
}