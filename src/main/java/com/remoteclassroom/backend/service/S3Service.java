package com.remoteclassroom.backend.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Value("${aws.bucketName}")
    private String bucketName;

    // ================= UPLOAD =================
    public String uploadFile(MultipartFile file) throws IOException {

        String fileName = "video-" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        return getFileUrl(fileName);
    }

    // ================= PRESIGNED UPLOAD =================
    public String generatePresignedUrl(String fileName) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(objectRequest)
                        .build();

        return s3Presigner.presignPutObject(presignRequest)
                .url()
                .toString();
    }

    // ================= FILE URL =================
    public String getFileUrl(String fileName) {
        return "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + fileName;
    }

    // ================= DOWNLOAD (NEW & SECURE) =================
    public String generateDownloadUrl(String fileUrl, String title) {

        // extract file name from URL
        String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        
        // Force browser to download instead of playing inline
        String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9.-]", "_");
        String contentDisposition = "attachment; filename=\"" + sanitizedTitle + ".mp4\"";

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .responseContentDisposition(contentDisposition)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // URL expires in 15 mins for security
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}