package com.remoteclassroom.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.remoteclassroom.backend.model.LiveClass;

public interface LiveClassRepository extends JpaRepository<LiveClass, Long> {

    List<LiveClass> findByIsLiveTrue();
}