package com.example.eventify;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvEventName;
    private TextView tvEventDescription;
    private TextView tvEventTheme;;
    private TextView tvDateTime;
    private ImageView ivEventImage;
    private Button btnInvite;
    private Button btnEdit;
    private Button btnDelete;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_detail);

        tvEventName=findViewById(R.id.tvEventName);
        tvEventTheme=findViewById(R.id.tvEventTheme);
        tvDateTime=findViewById(R.id.tvDateTime);
        ivEventImage=findViewById(R.id.ivtEventImage);
        btnInvite=findViewById(R.id.btnInvite);
        btnEdit=findViewById(R.id.btnEdit);
        btnDelete=findViewById(R.id.btnDelete);
    }
}
