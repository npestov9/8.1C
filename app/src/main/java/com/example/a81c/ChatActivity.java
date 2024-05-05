package com.example.a81c;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a81c.requests.ChatbotRequest;
import com.example.a81c.requests.ChatbotResponse;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private ListView messageListView;
    private EditText messageEditText;
    private Button sendButton;
    private ArrayAdapter<String> messageAdapter;
    private ArrayList<String> messages;
    private ChatbotService chatbotService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-url.com/") // Replace with your actual API URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        chatbotService = retrofit.create(ChatbotService.class);

        messageListView = findViewById(R.id.messageListView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        messages = new ArrayList<>();
        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messageListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    messageEditText.setText(""); // Clear the input box after sending
                }
            }
        });
    }

    private void sendMessage(String message) {
        ChatbotRequest request = new ChatbotRequest(message);

        chatbotService.sendMessage(request).enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(Call<ChatbotResponse> call, Response<ChatbotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update the UI with the response from the server
                    String reply = response.body().getReply();
                    messages.add("Bot: " + reply);
                    messageAdapter.notifyDataSetChanged();
                } else {
                    // Handle cases where the response is not successful
                    messages.add("Bot: Error - " + response.code() + " " + response.message());
                    messageAdapter.notifyDataSetChanged();
                    if (response.errorBody() != null) {
                        try {
                            // Log the error body to understand more about the error
                            Log.e("ChatActivity", "Error response body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("ChatActivity", "Error parsing error body", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatbotResponse> call, Throwable t) {
                // Handle failure such as no internet or server down
                messages.add("Bot: Failed to connect - " + t.getMessage());
                messageAdapter.notifyDataSetChanged();
                Log.e("ChatActivity", "Network call failed", t);
            }
        });
    }


}

