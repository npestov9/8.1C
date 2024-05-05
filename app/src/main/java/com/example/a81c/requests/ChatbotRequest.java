package com.example.a81c.requests;

public class ChatbotRequest {
    private final ChatMessage[] history;
    private final GenerationConfig config;

    public ChatbotRequest(ChatMessage[] history, GenerationConfig config) {
        this.history = history;
        this.config = config;
    }
}
