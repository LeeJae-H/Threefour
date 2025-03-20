package com.threefour.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class UserTimeInfo {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected UserTimeInfo() {}

    public UserTimeInfo(LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // 테스트를 위해 추가한 메서드입니다.
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
