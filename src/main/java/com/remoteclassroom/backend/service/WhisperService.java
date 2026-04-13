package com.remoteclassroom.backend.service;
import java.io.File;

public interface WhisperService {
    String transcribe(File file);
}