package com.example.a81c.requests;

public class GenerationConfig {
    private double temperature;
    private double topP;
    private int topK;
    private int maxOutputTokens;

    public GenerationConfig(double temperature, double topP, int topK, int maxOutputTokens) {
        this.temperature = temperature;
        this.topP = topP;
        this.topK = topK;
        this.maxOutputTokens = maxOutputTokens;
    }
}
