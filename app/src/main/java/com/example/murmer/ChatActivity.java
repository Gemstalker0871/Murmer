package com.example.murmer;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.murmer.adapter.ChatRecyclerAdapter;
import com.example.murmer.adapter.SearchUserRecyclerAdapter;
import com.example.murmer.model.ChatMessageModel;
import com.example.murmer.model.ChatroomModel;
import com.example.murmer.model.UserModel;
import com.example.murmer.utils.AndroidUtil;
import com.example.murmer.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBTN;
    ImageButton backBTN;
    TextView otherUsername;
    RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(),otherUser.getUserId());



        messageInput = findViewById(R.id.chat_msg_input);
        sendMessageBTN = findViewById(R.id.msg_send_btn);
        backBTN = findViewById(R.id.backbtn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);


        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();        //fishyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy
            }
        });

        sendMessageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageInput.getText().toString().trim();
                if (message.isEmpty()){
                    return;
                }
                sendMessageToUser(message);
            }
        });

        otherUsername.setText(otherUser.getUsername());


        getOrCreateChatroomModel();
        setupChatRecyclerView();
    }

    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    void sendMessageToUser(String message){
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);


        ChatMessageModel chatMessageModel = new ChatMessageModel(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });

    }

    void getOrCreateChatroomModel(){

        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    chatroomModel = task.getResult().toObject(ChatroomModel.class);
                    if (chatroomModel == null){
                        chatroomModel = new ChatroomModel(
                                chatroomId,
                                Arrays.asList(FirebaseUtil.currentUserId(),otherUser.getUserId()),
                                Timestamp.now(),
                                ""
                        );
                        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

                    }
                }
            }
        });

    }



}