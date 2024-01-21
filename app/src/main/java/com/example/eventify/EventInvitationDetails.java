package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class EventInvitationDetails extends AppCompatActivity {
    TextView EventName,EventType,OrganizerName,EventStart,EventEnd,Location,EventDescription,TextMessage;
    ImageView LocationImg;

    MediaPlayer mediaPlayer;
    Button PlayAudioBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_invitation_details);

        EventName=findViewById(R.id.eventname);
        EventType=findViewById(R.id.eventType);
        EventStart=findViewById(R.id.eventStart);
        EventEnd=findViewById(R.id.eventEnd);
        EventDescription=findViewById(R.id.eventDescription1);
        Location=findViewById(R.id.location);
        TextMessage=findViewById(R.id.textmessage);
        OrganizerName=findViewById(R.id.organizer);
        PlayAudioBtn=findViewById(R.id.PlayVoiceBtn);
        LocationImg=(ImageView) findViewById(R.id.LocationPhoto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        String eventKey = getIntent().getStringExtra("EventKey");
        String userId = getIntent().getStringExtra("User ID");
        Log.d("InvitationList", "userId " + userId);
        String eventName = getIntent().getStringExtra("EventName");
        String username = getIntent().getStringExtra("Username");
        String eventDescription = getIntent().getStringExtra("EventDescription");
        String eventStart = getIntent().getStringExtra("EventStart");
        String eventEnd = getIntent().getStringExtra("EventEnd");
        String eventLocation = getIntent().getStringExtra("EventLocation");
        String imageUrl = getIntent().getStringExtra("ImageUrl");
        String eventOrganizer = getIntent().getStringExtra("EventOrganizer");
        String eventType = getIntent().getStringExtra("EventType");

        EventName.setText(eventName);
        EventType.setText(eventType);
        EventStart.setText(eventStart);
        EventEnd.setText(eventEnd);
        EventDescription.setText(eventDescription);
        Location.setText(eventLocation);

        OrganizerName.setText(eventOrganizer);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(LocationImg);
        }

        DatabaseReference textMessageRef = FirebaseDatabase.getInstance().getReference()
                .child("invitations").child(eventKey).child("guests").child(userId).child("message");

        textMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String preTextMessage = dataSnapshot.getValue(String.class);
                // Set the previous text message to the EditText
                Log.d("InvitationList", "message " + preTextMessage);
                TextMessage.setText(preTextMessage);
            }
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        mediaPlayer = new MediaPlayer();
            DatabaseReference voiceMessageRef = FirebaseDatabase.getInstance().getReference()
                    .child("invitations").child(eventKey).child("guests").child(userId).child("voiceMessage");
            voiceMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String voiceMessageUrl = dataSnapshot.getValue(String.class);
                    setMediaPlayerDataSource(voiceMessageUrl);
                    // Set the voice message URL for later playback
                    Log.d("InvitationList", "Voice Message URL: " + voiceMessageUrl);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Initialize MediaPlayer for playing voice message


        // Set the audio attributes for MediaPlayer (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        }

        // Set onClickListener for PlayAudioBtn

        PlayAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start playing the voice message
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });
    }
    private void setMediaPlayerDataSource(String voiceMessageUrl) {
        if (voiceMessageUrl != null) {
            try {
                mediaPlayer.setDataSource(voiceMessageUrl);
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to prepare MediaPlayer", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }


    private void onNavigationIconClick() {
        Intent intent = new Intent(EventInvitationDetails.this, SideMenuActivity.class);
        startActivity(intent);
    }
}