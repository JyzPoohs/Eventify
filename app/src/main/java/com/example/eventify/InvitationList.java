package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InvitationList extends AppCompatActivity {
    RecyclerView invitationList;
    InvitationAdapter invitationAdapter;
    ArrayList<EventInvitation> invitation;
    DatabaseReference InvitationReference,invReference;
    DatabaseReference EventsReference;
    DatabaseReference UserReference;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });
        // Get the current user ID
        // Initialize FirebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId=user.getUid();
        Log.d("InvitationList", "Current User ID: " + currentUserId);

        invitationList = findViewById(R.id.InvitationRecycleView);
        invitationList.setLayoutManager(new LinearLayoutManager(InvitationList.this));
        InvitationReference = FirebaseDatabase.getInstance().getReference().child("invitations");
        EventsReference = FirebaseDatabase.getInstance().getReference().child("events");
        UserReference = FirebaseDatabase.getInstance().getReference().child("userinfo");
        invitation = new ArrayList<>();
        invitationAdapter = new InvitationAdapter(InvitationList.this, invitation);
        invitationList.setAdapter(invitationAdapter);


        InvitationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot invSnapshot : dataSnapshot.getChildren()) {
                    String eventKey = invSnapshot.getKey();



                    EventsReference.child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot eventSnapshot) {
                            if (eventSnapshot.exists()) {
                                String organizerId = eventSnapshot.child("userId").getValue(String.class);
                                Log.d("InvitationList", "Organizer ID: " + organizerId);

                                if(eventSnapshot.child("guests").child(currentUserId).exists()){
                                UserReference.child(organizerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        if (userSnapshot.exists()) {
                                            String organizerName = userSnapshot.child("username").getValue(String.class);
                                            Log.d("InvitationList", "Organizer Name: " + organizerName);

                                            // You can get other event information here
                                            String eventName = eventSnapshot.child("eventName").getValue(String.class);
                                            String eventDescription = eventSnapshot.child("eventDescription").getValue(String.class);
                                            String eventStartDate = eventSnapshot.child("eventStart").getValue(String.class);
                                            String eventEndDate = eventSnapshot.child("eventEnd").getValue(String.class);
                                            String eventLocation = eventSnapshot.child("eventLocation").getValue(String.class);
                                            String eventLocationImg = eventSnapshot.child("imageUrl").getValue(String.class);
                                            String eventType=eventSnapshot.child("eventType").getValue(String.class);
                                            String userId = dataSnapshot.child("guests").child(currentUserId).child("userId").getValue(String.class);
                                            String voiceMessage=eventSnapshot.child(eventKey).child("guests").child(currentUserId).child("audioKey").getValue(String.class);
                                            String textMessage=eventSnapshot.child(eventKey).child("guests").child(currentUserId).child("message").getValue(String.class);
                                            String username=eventSnapshot.child(eventKey).child("guests").child(currentUserId).child("username").getValue(String.class);
                                            Log.d("InvitationList", "Event Key: " + eventKey);
                                            Log.d("InvitationList", "textMessage:" + textMessage);
                                            Log.d("InvitationList", "userId:" + userId);
                                            Log.d("InvitationList","getKey:" + eventSnapshot.child(eventKey).child("guests").getKey());

                                            EventInvitation eventInvitation = new EventInvitation(eventKey, username, currentUserId, organizerName, eventName, eventLocation, eventLocationImg, eventDescription, textMessage, voiceMessage,eventStartDate,eventEndDate,eventType);
                                            invitation.add(eventInvitation);

                                            // Notify the adapter about the data changes
                                            invitationAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(InvitationList.this, "Something Wrong: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e("FirebaseError", error.getMessage(), error.toException());
                                    }
                                });
                            }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(InvitationList.this, "Something Wrong: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("FirebaseError", error.getMessage(), error.toException());
                        }
                    });
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InvitationList.this, "Something Wrong: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage(), error.toException());
            }
        });
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(InvitationList.this, SideMenuActivity.class);
        startActivity(intent);
    }
}