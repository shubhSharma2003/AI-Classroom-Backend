package com.remoteclassroom.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.remoteclassroom.backend.model.ClassParticipant;
import com.remoteclassroom.backend.model.LiveClass;
import com.remoteclassroom.backend.model.User;
import com.remoteclassroom.backend.repository.ClassParticipantRepository;
import com.remoteclassroom.backend.repository.LiveClassRepository;
import com.remoteclassroom.backend.repository.UserRepository;

@Service
public class LiveClassService {

    @Autowired
    private LiveClassRepository liveClassRepository;

    @Autowired
    private ClassParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    // ================= CREATE =================
    public LiveClass createClass(String title, String teacherEmail) {

        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LiveClass lc = new LiveClass();
        lc.setTitle(title);
        lc.setTeacher(teacher); // ✅ FIX
        lc.setLive(false);
        lc.setMeetingId(UUID.randomUUID().toString());

        return liveClassRepository.save(lc);
    }

    // ================= START =================
    public LiveClass startClass(Long classId) {
        LiveClass lc = liveClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        lc.setLive(true);
        lc.setStartTime(LocalDateTime.now());

        return liveClassRepository.save(lc);
    }

    // ================= JOIN =================
    public ClassParticipant joinClass(Long classId, String studentEmail) {

        LiveClass lc = liveClassRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (!lc.isLive()) {
            throw new RuntimeException("Class is not live");
        }

        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean alreadyJoined =
                participantRepository.existsByLiveClassIdAndStudentEmail(classId, studentEmail);

        if (alreadyJoined) {
            throw new RuntimeException("Already joined");
        }

        ClassParticipant cp = new ClassParticipant();
        cp.setLiveClass(lc);       // ✅ FIX
        cp.setStudent(student);    // ✅ FIX
        cp.setJoinedAt(LocalDateTime.now());

        return participantRepository.save(cp);
    }

    // ================= LEAVE =================
    public ClassParticipant leaveClass(Long classId, String studentEmail) {

        ClassParticipant cp = participantRepository
                .findByLiveClassIdAndStudentEmail(classId, studentEmail)
                .orElseThrow(() -> new RuntimeException("Not joined"));

        if (cp.getLeftAt() != null) {
            throw new RuntimeException("Already left");
        }

        cp.setLeftAt(LocalDateTime.now());
        cp.calculateDuration();

        return participantRepository.save(cp);
    }

    // ================= GET CLASS =================
    public LiveClass getClassById(Long id) {
        return liveClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));
    }

    // ================= LIVE CLASSES =================
    public List<LiveClass> getLiveClasses() {
        return liveClassRepository.findByIsLiveTrue();
    }

    // ================= TOTAL JOINED =================
    public long getAttendanceCount(Long classId) {
        return participantRepository.countByLiveClassId(classId);
    }

    // ================= REAL PRESENT =================
    public long getPresentCount(Long classId) {
        return participantRepository.findByLiveClassId(classId)
                .stream()
                .filter(p -> p.getDurationSeconds() != null && p.getDurationSeconds() > 60)
                .count();
    }

    // ================= CHECK JOIN =================
    public boolean isUserJoined(Long classId, String email) {
        return participantRepository.existsByLiveClassIdAndStudentEmail(classId, email);
    }
}