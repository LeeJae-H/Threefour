package com.threefour.domain.user;

public class JoinedEvent {

    private String email;
    private String nickname;

    public JoinedEvent(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }
}
