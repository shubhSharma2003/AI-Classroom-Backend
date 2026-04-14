package com.remoteclassroom.backend.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.Video;
import com.remoteclassroom.backend.repository.VideoRepository;

@Service
public class TranscriptionService {

    @Autowired
    private WhisperService whisperService;

    @Autowired
    private VideoRepository videoRepository;

    @Async
    public void transcribeVideoAsync(Video video) {

        File videoFile = null;
        File audioFile = null;

        try {
            System.out.println("🔥 ASYNC WORKER: Starting process for Video ID: " + video.getId());

            // 1. DOWNLOAD
            System.out.println("🔥 ASYNC WORKER: Downloading from S3...");
            videoFile = downloadFromUrl(video.getUrl());
            System.out.println("🔥 ASYNC WORKER: S3 Download Complete. Size = " + videoFile.length());

            // 2. CONVERT
            System.out.println("🔥 ASYNC WORKER: Running FFmpeg optimized extraction...");
            audioFile = convertToMp3(videoFile);
            System.out.println("🔥 ASYNC WORKER: FFmpeg extraction complete.");

            // 3. WHISPER
            System.out.println("🔥 ASYNC WORKER: Sending to Whisper API...");
            String transcript = whisperService.transcribe(audioFile);
            System.out.println("🔥 ASYNC WORKER: Whisper Transcription complete!");

            // 4. SAVE
            video.setTranscript(transcript);
            videoRepository.save(video);
            
            System.out.println("🔥 ASYNC WORKER: Succesfully saved transcript for Video ID: " + video.getId());

        } catch (Exception e) {
            System.err.println("❌ ASYNC WORKER FAILED for Video ID: " + video.getId());
            e.printStackTrace();
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
    // CONVERT TO MP3 (OPTIMIZED FOR WHISPER)
    // =========================
    private File convertToMp3(File videoFile) throws Exception {

        System.out.println("🎬 FFmpeg ASYNC START");

        File audioFile = File.createTempFile("audio_", ".mp3");

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", videoFile.getAbsolutePath(),
                // 🔥 NO MORE "-t 120" TRUNCATION LIMIT
                "-vn", // Strip video
                "-ac", "1", // Mono audio (Whisper uses mono anyway)
                "-ar", "16000", // 16kHz sampling rate is whisper's baseline
                "-b:a", "32k", // Low bitrate shrinks file extremely well under 25MB limits
                audioFile.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;
        while ((line = reader.readLine()) != null) {
            // Optional: Comment output to keep server logs clean
            System.out.println("FFMPEG: " + line);
        }

        int exitCode = process.waitFor();

        System.out.println("🎬 FFmpeg ASYNC END with code: " + exitCode);

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
        }

        return audioFile;
    }
}
