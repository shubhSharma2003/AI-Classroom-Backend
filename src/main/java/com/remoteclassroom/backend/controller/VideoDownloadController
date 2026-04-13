package com.remoteclassroom.backend.controller;

import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.VideoRepository;

@RestController
@RequestMapping("/api/video")
public class VideoDownloadController {

    @Autowired
    private VideoRepository videoRepository;

    @GetMapping("/download/{videoId}")
    public ResponseEntity<?> downloadVideo(@PathVariable Long videoId) {

        try {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found"));

            String fileUrl = video.getUrl();

            // 🔥 S3 se stream
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + video.getTitle() + ".mp4\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(inputStream.readAllBytes());

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Download failed: " + e.getMessage());
        }
    }
}