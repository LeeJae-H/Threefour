package com.threefour.domain.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class CommentTimeInfo {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected CommentTimeInfo() {}

    public CommentTimeInfo(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
