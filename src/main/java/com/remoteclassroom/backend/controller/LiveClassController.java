package com.remoteclassroom.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        String teacher = authentication.getName();
        return liveClassService.createClass(title, teacher);
    }

    @PostMapping("/start/{id}")
    public LiveClass startClass(@PathVariable Long id) {
        return liveClassService.startClass(id);
    }

    @PostMapping("/join/{id}")
public Object joinClass(
        @PathVariable Long id,
        Authentication authentication
) {
    String student = authentication.getName();

    liveClassService.joinClass(id, student);

    LiveClass lc = liveClassService.getClassById(id);

    return new java.util.HashMap<String, Object>() {{
        put("classId", id);
        put("meetingId", lc.getMeetingId());
        put("user", student);
    }};
}
    
    @GetMapping("/live")
    public List<LiveClass> getLiveClasses() {
        return liveClassService.getLiveClasses();
    }


    @GetMapping("/attendance/{id}")
    public long getAttendance(@PathVariable Long id) {
        return liveClassService.getAttendanceCount(id);
    }
}