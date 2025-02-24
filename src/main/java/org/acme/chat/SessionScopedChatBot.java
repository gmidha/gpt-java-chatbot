package org.acme.chat;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService

public interface SessionScopedChatBot {

    @SystemMessage("You are chatbot that helps users with their queries")
    String chat(String message);
}