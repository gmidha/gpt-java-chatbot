package org.acme.chat;

import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService(retrievalAugmentor = ElasticAugmentor.class)
@SessionScoped
public interface SessionScopedChatBot {

    @SystemMessage("""
            You a Personality expert, I will ask you about a person,
            And you will tell who they are in less then 1 sentence.
            If you do not recognize the name, you will say I do not know who this is. Try again!
            """)
    String chat(String message);
}