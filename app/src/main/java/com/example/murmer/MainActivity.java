package com.example.murmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    StatusFragment statusFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        statusFragment = new StatusFragment();





        bottomNavigationView = findViewById(R.id.bottommenu);
        searchButton = findViewById(R.id.mainsearchbtn);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menuchat) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainframlayout, chatFragment).commit();
                }
                if (item.getItemId() == R.id.menuprofile) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainframlayout, profileFragment).commit();
                }
                if (item.getItemId() == R.id.menustatus) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainframlayout, statusFragment).commit();
                }

                return true;
            }


        });
        bottomNavigationView.setSelectedItemId(R.id.menuchat);

    }
}