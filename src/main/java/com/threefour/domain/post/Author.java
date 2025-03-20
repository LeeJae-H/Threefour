package com.threefour.domain.post;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Author {

    @Column(name = "author_nickname")
    private String nickname;

    protected Author() {}

    public Author(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }
}
