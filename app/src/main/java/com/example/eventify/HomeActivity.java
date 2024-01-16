package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        CardView createEventLayout = findViewById(R.id.create_event_card);
        createEventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Code to navigate to another page (activity)
                Intent intent = new Intent(HomeActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onNavigationIconClick() {
        Intent intent = new Intent(HomeActivity.this, SideMenuActivity.class);
        startActivity(intent);
    };
}