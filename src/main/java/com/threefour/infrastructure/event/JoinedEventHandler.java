package com.threefour.infrastructure.event;

import com.threefour.domain.user.JoinedEvent;
import com.threefour.infrastructure.discord.DiscordClient;
import com.threefour.infrastructure.discord.DiscordMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class JoinedEventHandler {

    private final DiscordClient discordClient;

    @TransactionalEventListener(
            classes = JoinedEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(JoinedEvent event) {
        String content = "[회원가입]\n" +
                "이메일: " + event.getEmail() + "\n"
                + "닉네임: " + event.getNickname();
        discordClient.sendMessage(new DiscordMessage(content));
   }
}
