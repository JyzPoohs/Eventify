package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MessageDashboard extends AppCompatActivity {

    CardView sendMessage,viewEditMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        sendMessage=(CardView) findViewById(R.id.send_message_btn);
        viewEditMessage=(CardView) findViewById(R.id.View_Edit_Message_btn);


        String guestKey = getIntent().getStringExtra("GuestKey");
        String eventKey = getIntent().getStringExtra("EventKey");
        String userID = getIntent().getStringExtra("UserID");
        String username = getIntent().getStringExtra("Username");
        String contact = getIntent().getStringExtra("Contact");

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MessageDashboard", "GuestKey: " + guestKey);
                Log.d("MessageDashboard", "eventKey: " + eventKey);
                Log.d("MessageDashboard", "UserID: " + userID);
                Log.d("MessageDashboard", "Username: " + username);
                Log.d("MessageDashboard", "Contact: " + contact);
                Intent SendMessageIntent= new Intent(MessageDashboard.this, SendMessage.class);
                SendMessageIntent.putExtra("GuestKey",guestKey);
                SendMessageIntent.putExtra("Username",username);
                SendMessageIntent.putExtra("UserID",userID);
                SendMessageIntent.putExtra("EventKey",eventKey);
                SendMessageIntent.putExtra("Contact",contact);
                startActivity(SendMessageIntent);
            }
        });

        viewEditMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guestKey= getIntent().getStringExtra("GuestKey");
                String username= getIntent().getStringExtra("Username");
                String contact = getIntent().getStringExtra("Contact");
                Intent EditMessageIntent= new Intent(MessageDashboard.this, EditMessage.class);
                EditMessageIntent.putExtra("GuestKey",guestKey);
                EditMessageIntent.putExtra("Username",username);
                EditMessageIntent.putExtra("UserID",userID);
                EditMessageIntent.putExtra("EventKey",eventKey);
                EditMessageIntent.putExtra("Contact",contact);
                startActivity(EditMessageIntent);
            }
        });


    }
    private void onNavigationIconClick() {
        Intent intent = new Intent(MessageDashboard.this, SideMenuActivity.class);
        startActivity(intent);
    }
}