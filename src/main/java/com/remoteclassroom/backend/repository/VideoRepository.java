package com.remoteclassroom.backend.repository;

import com.remoteclassroom.backend.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByUploadedBy(String uploadedBy);
}