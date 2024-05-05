package com.example.a81c.requests;

public class ChatMessage {
    private String role;
    private String[] parts;

    public ChatMessage(String role, String[] parts) {
        this.role = role;
        this.parts = parts;
    }
}