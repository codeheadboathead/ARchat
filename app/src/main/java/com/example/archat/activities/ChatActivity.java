package com.example.archat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.archat.Models.User;
import com.example.archat.Models.chatMessage;
import com.example.archat.R;
import com.example.archat.activities.Adapter.chatAdapter;
import com.example.archat.databinding.ActivityChatBinding;
import com.example.archat.utilites.Constants;
import com.example.archat.utilites.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User reciveruser;
    private List<chatMessage> chatMessages ;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setListerners();
        loadReciverDetails();
        init();
    }


    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter chatAdapter = new chatAdapter(
                chatMessages,
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

    }

    private void sendMessage(){
        HashMap<String ,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID,reciveruser.id);
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        database.collection(Constants.KEY_COLLECTION_Users).add(message);
        binding.inputMessage.setText(null);
    }

    private void loadReciverDetails(){
        reciveruser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(reciveruser.name);
    }

    private void setListerners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutsend.setOnClickListener(v -> sendMessage());
    }

}