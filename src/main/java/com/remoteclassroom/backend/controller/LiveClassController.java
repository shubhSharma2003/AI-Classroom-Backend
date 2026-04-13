package com.remoteclassroom.backend.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.remoteclassroom.backend.dto.LiveClassResponse;
import com.remoteclassroom.backend.model.ClassParticipant;
import com.remoteclassroom.backend.model.LiveClass;
import com.remoteclassroom.backend.service.LiveClassService;

@RestController
@RequestMapping("/api/class")
public class LiveClassController {

    @Autowired
    private LiveClassService liveClassService;

    @PostMapping("/create")
    public LiveClass createClass(
            @RequestParam String title,
            Authentication authentication
    ) {
        return liveClassService.createClass(title, authentication.getName());
    }

    @GetMapping("/token/{id}")
    public Object getToken(
            @PathVariable Long id,
            Authentication auth
    ) {

        String user = auth.getName();

        LiveClass lc = liveClassService.getClassById(id);

        if (!lc.isLive()) {
            throw new RuntimeException("Class is not live");
        }

        // ✅ FIX: compare email instead of object
        boolean isTeacher = lc.getTeacher().getEmail().equals(user);

        if (!isTeacher && !liveClassService.isUserJoined(id, user)) {
            throw new RuntimeException("Join class first");
        }

        String role = isTeacher ? "HOST" : "AUDIENCE";

        String channel = lc.getMeetingId();

        String token = "TEMP_TOKEN_" + user;

        HashMap<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("channel", channel);
        res.put("role", role);
        res.put("user", user);

        return res;
    }

    @PostMapping("/start/{id}")
public Object startClass(@PathVariable Long id) {

    LiveClass lc = liveClassService.startClass(id);

    return new com.remoteclassroom.backend.dto.LiveClassResponse(
            lc.getId(),
            lc.getTitle(),
            lc.getTeacher().getEmail(),
            lc.isLive(),
            lc.getMeetingId()
    );
}

    @PostMapping("/join/{id}")
    public Object joinClass(@PathVariable Long id, Authentication auth) {

        String student = auth.getName();

        liveClassService.joinClass(id, student);
        LiveClass lc = liveClassService.getClassById(id);

        HashMap<String, Object> res = new HashMap<>();
        res.put("classId", id);
        res.put("meetingId", lc.getMeetingId());
        res.put("user", student);

        return res;
    }

    @PostMapping("/leave/{id}")
    public Object leaveClass(@PathVariable Long id, Authentication auth) {

        String student = auth.getName();

        ClassParticipant cp = liveClassService.leaveClass(id, student);

        HashMap<String, Object> res = new HashMap<>();
        res.put("classId", id);
        res.put("student", student);
        res.put("durationSeconds", cp.getDurationSeconds());

        return res;
    }

    @GetMapping("/live")
public List<LiveClassResponse> getLiveClasses() {

    return liveClassService.getLiveClasses().stream()
            .map(lc -> new LiveClassResponse(
                    lc.getId(),
                    lc.getTitle(),
                    lc.getTeacher().getEmail(),
                    lc.isLive(),
                    lc.getMeetingId()
            ))
            .toList();
}

    @GetMapping("/attendance/{id}")
    public long getAttendance(@PathVariable Long id) {
        return liveClassService.getAttendanceCount(id);
    }

    @GetMapping("/attendance/real/{id}")
    public long getRealAttendance(@PathVariable Long id) {
        return liveClassService.getPresentCount(id);
    }
}