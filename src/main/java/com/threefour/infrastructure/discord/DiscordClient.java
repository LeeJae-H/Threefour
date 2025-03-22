package com.threefour.infrastructure.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "discord-event-client", url = "${discord.webhook-url.base}${discord.webhook-url.event}")
public interface DiscordClient {

    @PostMapping
    void sendMessage(@RequestBody DiscordMessage discordMessage);
}
