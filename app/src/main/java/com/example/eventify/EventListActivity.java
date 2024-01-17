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

public class EventListActivity extends AppCompatActivity {

    EventsAdapter eventsAdapter;
    RecyclerView eventsRecyclerView;
    ArrayList<Event> events;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        eventsRecyclerView = findViewById(R.id.EventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(EventListActivity.this));
        // Get the current user's UID
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Adjust the DatabaseReference to filter events for the current user
        myRef = FirebaseDatabase.getInstance().getReference().child("events");
        Query query = myRef.orderByChild("userId").equalTo(currentUserUid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Event p = item.getValue(Event.class);
                    events.add(p);
                }
                eventsAdapter = new EventsAdapter(EventListActivity.this, events);
                eventsRecyclerView.setAdapter(eventsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventListActivity.this, "Something Wrong: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage(), error.toException());
            }
        });

    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(EventListActivity.this, SideMenuActivity.class);
        startActivity(intent);
    }
}