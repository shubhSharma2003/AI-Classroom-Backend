package com.remoteclassroom.backend.dto;

public class LiveClassResponse {

    private Long id;
    private String title;
    private String teacherEmail;
    private boolean live;
    private String meetingId;

    public LiveClassResponse(Long id, String title, String teacherEmail, boolean live, String meetingId) {
        this.id = id;
        this.title = title;
        this.teacherEmail = teacherEmail;
        this.live = live;
        this.meetingId = meetingId;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getTeacherEmail() { return teacherEmail; }
    public boolean isLive() { return live; }
    public String getMeetingId() { return meetingId; }
}