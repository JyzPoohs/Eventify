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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfGuest extends AppCompatActivity {

    RecyclerView GuestList;
    ParticipantAdapter participantAdapter;
    ArrayList<Participants> participants;
    DatabaseReference GuestReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_guest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        GuestList = findViewById(R.id.GuestList);
        GuestList.setLayoutManager(new LinearLayoutManager(ListOfGuest.this));

        String id = getIntent().getStringExtra("EventKey");
        Log.d("FirebaseData", "EventKey" + id);

        GuestReference = FirebaseDatabase.getInstance().getReference().child("events").child(id).child("guests");

        // Initialize the adapter once in onCreate
        participants = new ArrayList<>();
        GuestReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                participants = new ArrayList<>();
                participantAdapter = new ParticipantAdapter(ListOfGuest.this, participants);
                GuestList.setAdapter(participantAdapter);
                for (DataSnapshot guestSnapshot : dataSnapshot.getChildren()) {
                    String guestKey = guestSnapshot.getKey();
                    String contactNumber = guestSnapshot.child("contact").getValue(String.class);
                    String username = guestSnapshot.child("username").getValue(String.class);

                    Participants participant = new Participants(username, contactNumber);
                    participants.add(participant);
                }

                Log.d("FirebaseData", "Participants size: " + participants.size());
                if (!participants.isEmpty()) {
                    // Set the adapter and notify data changes
                    participantAdapter = new ParticipantAdapter(ListOfGuest.this, participants);
                    GuestList.setAdapter(participantAdapter);
                    participantAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListOfGuest.this, "Something Wrong: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage(), error.toException());
            }
        });
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(ListOfGuest.this, SideMenuActivity.class);
        startActivity(intent);
    }
}
