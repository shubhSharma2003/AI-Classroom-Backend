package com.remoteclassroom.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "live_classes")
public class LiveClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String teacher;

    private boolean isLive;

    private LocalDateTime startTime;

    private String meetingId;

    public LiveClass() {}

    // Getters
    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getTeacher() { return teacher; }

    public boolean isLive() { return isLive; }

    public LocalDateTime getStartTime() { return startTime; }

    public String getMeetingId() { return meetingId; }

    
    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setTeacher(String teacher) { this.teacher = teacher; }

    public void setLive(boolean live) { isLive = live; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }
}