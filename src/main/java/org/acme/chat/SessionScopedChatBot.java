package org.acme.chat;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService(retrievalAugmentor = ElasticAugmentor.class)
@SessionScoped
public interface SessionScopedChatBot {

    @SystemMessage("""
            You are a helpful assistant.
            You are an expert technical consultant about OpenShift and AI.
            Always provide concise and professional responses.
            """)
    String chat(String message);
}