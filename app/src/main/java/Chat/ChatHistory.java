package Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import Map.MapPage;
import ru.streetteam.app.MainPage;
import ru.streetteam.app.R;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.HistoryRoomListener;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Message;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.SubscribeOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

import lombok.Data;

public class ChatHistory extends AppCompatActivity implements RoomListener {

    private String roomName;
    private String roomTitle;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private String channelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_history);
        System.out.println("                                                                     ");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                                 Chat open");
        System.out.println("--------------------------------------------------------------------");


        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        ChatActivity.MemberData data = new ChatActivity.MemberData("History", "#673AB7");
        Bundle chatInfo = getIntent().getExtras();
        roomTitle = (String) chatInfo.get("roomTitle");
        channelId = (String) chatInfo.get("channelId");
        roomName = (String) chatInfo.get("roomName");
        scaledrone = new Scaledrone(channelId, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Соединение с Scaledrone открыто ");
                Room room = scaledrone.subscribe(roomName,
                        ChatHistory.this,
                        new SubscribeOptions(20));
                room.listenToHistoryEvents(new HistoryRoomListener() {
                    @Override
                    public void onHistoryMessage(Room room, com.scaledrone.lib.Message message) {
                        scaledrone.publish(room.getName(), message.getData());
                    }
                });
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });
    }

    @Override
    public void onOpen(Room room) {

    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {

    }

    @Override
    public void onMessage(Room room, Message receivedMessage) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final ChatActivity.MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), ChatActivity.MemberData.class);
            boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
            final Chat.Message message = new Chat.Message(receivedMessage.getData().asText(), data, belongsToCurrentUser);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // Обработчик нажатия кнопки "назад"
    public void buttonClickBack(View view) {
        Intent intent = new Intent(ChatHistory.this, ChatActivity.class);
        Bundle chatInfo = new Bundle();
        chatInfo.putString("channelId", channelId);
        chatInfo.putString("roomName", roomName);
        chatInfo.putString("roomTitle", roomTitle);
        intent.putExtras(chatInfo);
        startActivity(intent);
    }
}