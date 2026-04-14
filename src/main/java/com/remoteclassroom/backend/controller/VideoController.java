package com.remoteclassroom.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // ================= UPLOAD =================
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            Authentication authentication
    ) {
        try {
            String fileUrl = s3Service.uploadFile(file);
            String username = authentication.getName();

            Video savedVideo = videoService.saveVideo(
                    title,
                    fileUrl,
                    username,
                    null
            );

            return ResponseEntity.ok(savedVideo);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    // ================= PRESIGNED =================
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/upload-url")
    public ResponseEntity<?> getUploadUrl(@RequestParam String fileName) {

        String uniqueFileName = "video-" + UUID.randomUUID() + "-" + fileName;

        String uploadUrl = s3Service.generatePresignedUrl(uniqueFileName);
        String fileUrl = s3Service.getFileUrl(uniqueFileName);

        return ResponseEntity.ok(Map.of(
                "uploadUrl", uploadUrl,
                "fileUrl", fileUrl,
                "fileName", uniqueFileName
        ));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/save")
    public ResponseEntity<?> saveVideo(
            @RequestParam String title,
            @RequestParam String url,
            @RequestParam(required = false) String transcript,
            Authentication authentication
    ) {

        String username = authentication.getName();

        Video savedVideo = videoService.saveVideo(
                title,
                url,
                username,
                transcript
        );

        return ResponseEntity.ok(savedVideo);
    }

    // ================= DOWNLOAD (🔥 IMPORTANT) =================
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/download/{videoId}")
    public ResponseEntity<?> downloadVideo(@PathVariable Long videoId) {

        Video video = videoService.getById(videoId);

        String downloadUrl = s3Service.generateDownloadUrl(video.getUrl(), video.getTitle());

        return ResponseEntity.ok(Map.of(
                "downloadUrl", downloadUrl,
                "title", video.getTitle()
        ));
    }

    // ================= LIST =================
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