package com.example.murmer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.murmer.ChatActivity;
import com.example.murmer.model.UserModel;
import com.example.murmer.utils.AndroidUtil;
import com.example.murmer.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import com.example.murmer.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;


    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchUserRecyclerAdapter.UserModelViewHolder holder, int position, @NonNull UserModel model) {

        String username = model.getUsername();
        String phone = model.getPhone();
        String currentUserId = FirebaseUtil.currentUserId();


        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());

        if(model.getUserId().equals(FirebaseUtil.currentUserId())){

            holder.usernameText.setText(model.getUsername() + " (Me) ");

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                AndroidUtil.passUserModelAsIntent(intent,model);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public SearchUserRecyclerAdapter.UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);

        return new UserModelViewHolder(view);
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{

        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_txt);
            phoneText = itemView.findViewById(R.id.phone_txt);
            profilePic = itemView.findViewById(R.id.profile_pic_img_view);
        }
        }
    }
