package com.example.murmer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.murmer.ChatActivity;
import com.example.murmer.model.ChatMessageModel;
import com.example.murmer.utils.AndroidUtil;
import com.example.murmer.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import com.example.murmer.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;


    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRecyclerAdapter.ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {

        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText(model.getMessage());
        }
        else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.leftChatTextView.setText(model.getMessage());

        }

    }

    @NonNull
    @Override
    public ChatRecyclerAdapter.ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);

        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextView,rightChatTextView;



        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_txt);
            rightChatTextView = itemView.findViewById(R.id.right_chat_txt);

        }
    }
}
