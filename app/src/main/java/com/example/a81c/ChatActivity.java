package com.example.a81c;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendButton;
    private TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        responseText = findViewById(R.id.responseText);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(messageEditText.getText().toString());
            }
        });
    }

    private void sendMessage(String userMessage) {
        final String xml = "<chat application='8332384265189409422' instance='165'><message>" + userMessage + "</message></chat>";
        final MediaType MEDIA_TYPE_XML = MediaType.parse("application/xml; charset=utf-8");

        // Creating a new thread to handle network operations
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(xml, MEDIA_TYPE_XML);
                Request request = new Request.Builder()
                        .url("https://www.botlibre.com/rest/api/chat")
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseText.setText(responseData);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
