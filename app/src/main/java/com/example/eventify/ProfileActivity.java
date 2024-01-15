package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    TextView username, email, contact;
    Button edit_profile_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        username = findViewById(R.id.profile_username);
        email = findViewById(R.id.profile_email);
        contact = findViewById(R.id.profile_contact);
        edit_profile_btn = findViewById(R.id.profile_edit_btn);

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(ProfileActivity.this, SideMenuActivity.class);
        startActivity(intent);
    }
}