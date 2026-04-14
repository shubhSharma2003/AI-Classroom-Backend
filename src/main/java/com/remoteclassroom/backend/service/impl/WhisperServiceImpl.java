package com.remoteclassroom.backend.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.remoteclassroom.backend.service.WhisperService;

@Service
public class WhisperServiceImpl implements WhisperService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String URL = "https://api.openai.com/v1/audio/transcriptions";

    @Override
    public String transcribe(File file) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.FileSystemResource(file));
        body.add("model", "whisper-1");

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(URL, request, String.class);

        // Whisper returns JSON like: {"text": "...transcript..."}
        // We must extract just the text field, not the raw JSON wrapper
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("text").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Whisper response: " + response.getBody(), e);
        }
    }
}