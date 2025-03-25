package com.threefour.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Author {

    @Column(name = "user_id")
    private Long userId;

    protected Author() {}

    public Author(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}