package com.remoteclassroom.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "weak_topic")
public class WeakTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")   
    private Long userId;

    private String topic;

    private int count;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTopic() { return topic; }
    public int getCount() { return count; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setCount(int count) { this.count = count; }
}