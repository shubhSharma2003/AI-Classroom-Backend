package com.remoteclassroom.backend.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    private final String bucketName = "remote-classroom-videos-02";

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
                RequestBody.fromBytes(file.getBytes())
        );

        return getFileUrl(fileName);
    }

    // ================= PRESIGNED UPLOAD =================
    public String generatePresignedUrl(String fileName) {

        S3Presigner presigner = S3Presigner.create();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(objectRequest)
                        .build();

        return presigner.presignPutObject(presignRequest)
                .url()
                .toString();
    }

    // ================= FILE URL =================
    public String getFileUrl(String fileName) {
        return "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + fileName;
    }

    // ================= DOWNLOAD (NEW) =================
    public String generateDownloadUrl(String fileUrl) {

        // extract file name from URL
        String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        return "https://" + bucketName + ".s3.ap-south-1.amazonaws.com/" + key;
    }
}