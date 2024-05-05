package com.example.a81c;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

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
                        runOnUiThread(() -> responseText.setText(extractedMessage));
                    } else {
                        runOnUiThread(() -> responseText.setText("Error: " + response.code()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> responseText.setText("Failed to connect: " + e.getMessage()));
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

}
