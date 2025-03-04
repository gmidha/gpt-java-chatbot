package org.acme.chat;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.enterprise.context.ApplicationScoped;


@WebSocket(path = "/chat/{username}")
@ApplicationScoped
public class ChatSocket {

    private final SessionScopedChatBot bot;

    public ChatSocket(SessionScopedChatBot bot) {
        this.bot = bot;
    }

    @OnOpen
    public String onOpen() {
        return "Hello, how can I help you?";
    }

    @OnTextMessage
    public String onMessage(String message) {
        return bot.chat(message);
    }


}