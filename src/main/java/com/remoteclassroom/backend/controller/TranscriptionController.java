package com.remoteclassroom.backend.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.VideoRepository;
import com.remoteclassroom.backend.service.WhisperService;

@RestController
@RequestMapping("/api/transcribe")
public class TranscriptionController {

    @Autowired
    private WhisperService whisperService;

    @Autowired
    private VideoRepository videoRepository;

    @PostMapping("/{videoId}")
public ResponseEntity<?> transcribe(
        @PathVariable Long videoId,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force
) {

    File videoFile = null;
    File audioFile = null;

    try {
        System.out.println("🔥 STEP 0: API HIT");

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        System.out.println("🔥 STEP 1: Video fetched from DB");

        // CACHE CHECK
        if (!force && video.getTranscript() != null && !video.getTranscript().isBlank()) {
            System.out.println("🔥 STEP 2: Transcript already exists (CACHE HIT)");
            return ResponseEntity.ok(video.getTranscript());
        }

        // DOWNLOAD
        System.out.println("🔥 STEP 3: Downloading from S3...");
        videoFile = downloadFromUrl(video.getUrl());
        System.out.println("🔥 STEP 3 DONE: File size = " + videoFile.length());

        // CONVERT
        System.out.println("🔥 STEP 4: Converting to MP3...");
        audioFile = convertToMp3(videoFile);
        System.out.println("🔥 STEP 4 DONE");

        // WHISPER
        System.out.println("🔥 STEP 5: Sending to Whisper...");
        String transcript = whisperService.transcribe(audioFile);
        System.out.println("🔥 STEP 5 DONE");

        // SAVE
        video.setTranscript(transcript);
        videoRepository.save(video);

        System.out.println("🔥 STEP 6: Saved to DB");

        return ResponseEntity.ok(transcript);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError()
                .body("Error: " + e.getMessage());
    } finally {
        if (videoFile != null) videoFile.delete();
        if (audioFile != null) audioFile.delete();
    }
}

    // =========================
    // DOWNLOAD FROM S3
    // =========================
    private File downloadFromUrl(String urlStr) throws Exception {

        URL url = new URL(urlStr);

        File file = File.createTempFile("video_", ".tmp");

        try (InputStream in = url.openStream()) {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return file;
    }

    // =========================
    // CONVERT TO MP3 (SAFE)
    // =========================
    private File convertToMp3(File videoFile) throws Exception {

    System.out.println("🎬 FFmpeg START");

    File audioFile = File.createTempFile("audio_", ".mp3");

    ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",

            // 🔥 START FROM BEGINNING (important)
            "-ss", "0",

            // 🔥 INPUT
            "-i", videoFile.getAbsolutePath(),

            // 🔥 LIMIT BEFORE PROCESSING (FASTER)
            "-t", "120",

            "-vn",
            "-ac", "1",
            "-ar", "16000",
            "-b:a", "32k",

            audioFile.getAbsolutePath()
    );

    pb.redirectErrorStream(true);

    Process process = pb.start();

    BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream())
    );

    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println("FFMPEG: " + line);
    }

    int exitCode = process.waitFor();

    System.out.println("🎬 FFmpeg END with code: " + exitCode);

    if (exitCode != 0) {
        throw new RuntimeException("FFmpeg failed");
    }

    return audioFile;
}
}