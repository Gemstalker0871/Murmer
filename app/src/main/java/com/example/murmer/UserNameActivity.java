package com.example.murmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.murmer.model.UserModel;
import com.example.murmer.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserNameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button texting_btn;
    ProgressBar progressBar;
    String phoneno;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_name);

        usernameInput = findViewById(R.id.username_input);
        texting_btn = findViewById(R.id.start_texting_btn);
        progressBar = findViewById(R.id.progressbarwow);

        phoneno = getIntent().getExtras().getString("phone");
        getUsername();

        texting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUsername();
            }
        });


    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            texting_btn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            texting_btn.setVisibility(View.VISIBLE);
        }
    }
    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }

        });
    }

    void setUsername(){

        String username = usernameInput.getText().toString();
        if( username.isEmpty() || username.length() < 3 ){
            usernameInput.setError("Username should be atleast 3 Char");
            return;
        }
        setInProgress(true);

        if (userModel != null){
            userModel.setUsername(username);
        }
        else{
            userModel = new UserModel(username, Timestamp.now(),phoneno);
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(UserNameActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}