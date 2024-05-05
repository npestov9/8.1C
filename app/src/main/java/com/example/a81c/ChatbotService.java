package com.example.a81c;

import com.example.a81c.requests.ChatbotRequest;
import com.example.a81c.requests.ChatbotResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChatbotService {
    @Headers("Authorization: Bearer AIzaSyDnZrhn98daHCPTjT0p1AizymnNO4JvkfA")
    @POST("v1/projects/your_project_id/locations/global/models/gemini-1.5-pro-latest:generate")
    Call<ChatbotResponse> sendMessage(@Body ChatbotRequest request);
}
