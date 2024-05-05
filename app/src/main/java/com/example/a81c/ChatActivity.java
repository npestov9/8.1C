package com.example.a81c;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
                    messages.add("You: " + message);
                    messageAdapter.notifyDataSetChanged();
                    messageEditText.setText("");
                    sendMessageToServer(message);
                }
            }
        });
    }

    private void sendMessageToServer(String userMessage) {
        final String xml = "<chat application='8332384265189409422' instance='165'><message>" + userMessage + "</message></chat>";
        final MediaType MEDIA_TYPE_XML = MediaType.parse("application/xml; charset=utf-8");

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
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        String extractedMessage = parseXML(responseData);
                        displayServerResponse(extractedMessage);
                    } else {
                        runOnUiThread(() -> {
                            messages.add("Bot: Error - " + response.code());
                            messageAdapter.notifyDataSetChanged();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        messages.add("Bot: Failed to connect: " + e.getMessage());
                        messageAdapter.notifyDataSetChanged();
                    });
                }
            }
        }).start();
    }

    private String parseXML(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(xmlData));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "message".equals(parser.getName())) {
                    if (parser.next() == XmlPullParser.TEXT) {
                        return parser.getText();  // Return the text inside <message>
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Log.e("ChatActivity", "Error parsing XML", e);
            return "Error parsing response";
        }
        return "No message found";
    }

    private void displayServerResponse(String message) {
        runOnUiThread(() -> {
            messages.add("Bot: " + message);
            messageAdapter.notifyDataSetChanged();
        });
    }
}
