package com.example.murmer;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.murmer.adapter.SearchUserRecyclerAdapter;
import com.example.murmer.model.UserModel;
import com.example.murmer.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchBTN;
    ImageButton backBTN;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);


        searchInput = findViewById(R.id.search_username_bar);
        searchBTN = findViewById(R.id.search_user_btn);
        backBTN = findViewById(R.id.backbtn);
        recyclerView = findViewById(R.id.search_user_recyclerview);

        searchInput.requestFocus();

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getOnBackPressedDispatcher().onBackPressed();            //fishyyyyyyyyyyyyyyyyyyyyyyyyyy
            }
        });

        searchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTerm = searchInput.getText().toString();

                if(searchTerm.isEmpty() || searchTerm.length()<3 ){

                searchInput.setError("Invalid Username");
                return;
                }
                setupSearchRecyclerView(searchTerm);

            }
        });


    }

    void setupSearchRecyclerView(String searchTerm){



        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username",searchTerm);


        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }


}