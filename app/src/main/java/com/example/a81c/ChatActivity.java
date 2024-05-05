package com.example.a81c;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendButton;
    private ListView messageListView;
    private ArrayAdapter<String> messageAdapter;
    private ArrayList<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        messageListView = findViewById(R.id.messageListView);

        messages = new ArrayList<>();
        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        messageListView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (!message.isEmpty()) {
                    messages.add("You: " + message);  // Add user message to the list
                    messageAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the list view
                    messageEditText.setText("");  // Clear the input box after sending
                    sendMessageToServer(message);  // Method to send the message to the server
                }
            }
        });
    }

    private void sendMessageToServer(String userMessage) {
        // Here, you would add your network call logic to send the message to the server
        // For now, let's simulate a server response
        String simulatedResponse = "This is a simulated response from the server.";
        displayServerResponse(simulatedResponse);
    }

    private void displayServerResponse(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages.add("Bot: " + message);  // Add server response to the list
                messageAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the list view
            }
        });
    }
}
