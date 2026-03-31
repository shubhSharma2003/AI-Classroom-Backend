package com.remoteclassroom.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.ClassParticipant;
import com.remoteclassroom.backend.model.LiveClass;
import com.remoteclassroom.backend.repository.ClassParticipantRepository;
import com.remoteclassroom.backend.repository.LiveClassRepository;

@Service
public class LiveClassService {

    @Autowired
    private LiveClassRepository liveClassRepository;

    @Autowired
    private ClassParticipantRepository participantRepository;

    // ✅ Create class (UPDATED with meetingId)
    public LiveClass createClass(String title, String teacher) {
        LiveClass lc = new LiveClass();
        lc.setTitle(title);
        lc.setTeacher(teacher);
        lc.setLive(false);

        // 🔥 Generate meetingId for streaming
        lc.setMeetingId(UUID.randomUUID().toString());

        return liveClassRepository.save(lc);
    }

    // ✅ Start class
    public LiveClass startClass(Long classId) {
        LiveClass lc = liveClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        lc.setLive(true);
        lc.setStartTime(LocalDateTime.now());

        return liveClassRepository.save(lc);
    }

    // ✅ Join class
    public ClassParticipant joinClass(Long classId, String student) {

        // 1. Check class exists
        LiveClass lc = liveClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // 2. Check if class is live
        if (!lc.isLive()) {
            throw new RuntimeException("Class is not live");
        }

        // 3. Prevent duplicate join
        boolean alreadyJoined = participantRepository
                .existsByClassIdAndStudent(classId, student);

        if (alreadyJoined) {
            throw new RuntimeException("Student already joined this class");
        }

        // 4. Save participant
        ClassParticipant cp = new ClassParticipant();
        cp.setClassId(classId);
        cp.setStudent(student);
        cp.setJoinedAt(LocalDateTime.now());

        return participantRepository.save(cp);
    }

    // ✅ Get live classes
    public List<LiveClass> getLiveClasses() {
        return liveClassRepository.findByIsLiveTrue();
    }

    // 🔥 NEW METHOD (ADD THIS)
    public LiveClass getClassById(Long id) {
        return liveClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));
    }

    // ✅ Attendance count
    public long getAttendanceCount(Long classId) {
        return participantRepository.countByClassId(classId);
    }
}