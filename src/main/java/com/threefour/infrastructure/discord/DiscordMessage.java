package com.threefour.infrastructure.discord;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiscordMessage {

    private String content;

    public DiscordMessage(String content) {
        this.content = content;
    }
}
