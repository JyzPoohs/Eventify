package com.example.eventify;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvEventName;
    private TextView tvEventDescription;
    private TextView tvEventTheme;
    private TextView tvEventLocation;
    private TextView tvDateTime;
    private ImageView ivEventImage;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_detail);
    }
}
