package com.remoteclassroom.backend.repository;

import com.remoteclassroom.backend.model.LiveClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiveClassRepository extends JpaRepository<LiveClass, Long> {

    List<LiveClass> findByIsLiveTrue();
}