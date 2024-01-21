package com.example.eventify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvEventName;
    private TextView tvEventDescription;
    private TextView tvUsername;
    private TextView tvStart;
    private TextView tvEnd;
    private TextView tvLocation;
    private TextView tvContact;
    private ImageView ivEventImage;
    private Button btnInvite;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnViewParticipant;
    DatabaseReference myRef;
    String id, eventName, eventDescription, eventStart, eventEnd, eventLocation, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        tvEventName = findViewById(R.id.tvEventName);
        tvStart = findViewById(R.id.tvStart);
        tvEnd = findViewById(R.id.tvEnd);
        tvUsername = findViewById(R.id.tvUsername);
        tvContact = findViewById(R.id.tvContact);
        tvEventDescription = findViewById(R.id.tvEventDescription);
        tvLocation = findViewById(R.id.tvLocation);
        ivEventImage = findViewById(R.id.ivEventImage);
        btnInvite = findViewById(R.id.btnInvite);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnViewParticipant = findViewById(R.id.btnViewParticipant);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("userinfo");
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersRef.child(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve user information
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String contact = dataSnapshot.child("contact").getValue(String.class);
                    tvUsername.setText(username);
                    tvContact.setText(contact);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef = FirebaseDatabase.getInstance().getReference().child("events");

        getIntentData();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(ivEventImage);
        }

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent InviteIntent = new Intent(EventDetailActivity.this, Invitation.class);
                InviteIntent.putExtra("EventKey", id);


                // Start EditEventActivity
                startActivity(InviteIntent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent editIntent = new Intent(EventDetailActivity.this, EventUpdateActivity.class);

                editIntent.putExtra("EventKey", id);
                editIntent.putExtra("EventName", eventName);
                editIntent.putExtra("EventDescription", eventDescription);
                editIntent.putExtra("EventLocation", eventLocation);
                editIntent.putExtra("Start", eventStart);
                editIntent.putExtra("End", eventEnd);
                editIntent.putExtra("ImageUrl", imageUrl);

                // Start EditEventActivity
                startActivity(editIntent);
            }
        });

        btnViewParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent participantIntent=new Intent(EventDetailActivity.this,ListOfGuest.class);
                participantIntent.putExtra("EventKey", id);
                startActivity(participantIntent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    void getIntentData(){
        if(getIntent().hasExtra("EventKey") && getIntent().hasExtra("EventName") && getIntent().hasExtra("Start")
                && getIntent().hasExtra("End") && getIntent().hasExtra("EventDescription") && getIntent().hasExtra("EventLocation")
                && getIntent().hasExtra("ImageUrl")){
            id = getIntent().getStringExtra("EventKey");
            eventName = getIntent().getStringExtra("EventName");
            eventStart = getIntent().getStringExtra("Start");
            eventEnd = getIntent().getStringExtra("End");
            eventLocation = getIntent().getStringExtra("EventLocation");
            eventDescription = getIntent().getStringExtra("EventDescription");
            imageUrl = getIntent().getStringExtra("ImageUrl");

            tvEventName.setText(eventName);
            tvStart.setText(eventStart);
            tvEnd.setText(eventEnd);
            tvEventDescription.setText(eventDescription);
            tvLocation.setText(eventLocation);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + eventName + " ?");
        builder.setMessage("Are you sure you want to delete " + eventName + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the event data from Realtime Database
                myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event event = dataSnapshot.getValue(Event.class);

                            // Delete the image from Firebase Storage
                            if (event != null && event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(event.getImageUrl());
                                storageRef.delete();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // Remove the event from Realtime Database
                myRef.child(id).removeValue();

                Intent intent = new Intent(EventDetailActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(EventDetailActivity.this, SideMenuActivity.class);
        startActivity(intent);
    }

}