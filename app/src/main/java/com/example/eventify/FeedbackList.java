package com.example.eventify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackList extends AppCompatActivity {

    private TextView tvFeedbackListEventName, tvFeedbackListUsername, tvFeedbackListEventDescription, tvFeedbackListLocationName;
    private RecyclerView recyclerViewFeedbacks;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;
    private DatabaseReference feedbackReference;
    private DatabaseReference eventsReference;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        tvFeedbackListEventName = findViewById(R.id.tv_feedbacklist_eventname);
        tvFeedbackListUsername = findViewById(R.id.tv_feedbacklist_username);
        tvFeedbackListEventDescription = findViewById(R.id.tv_feedbacklist_event_description);
        tvFeedbackListLocationName = findViewById(R.id.tv_feedbacklist_location_name);

        recyclerViewFeedbacks = findViewById(R.id.recyclerViewFeedbacks);
        recyclerViewFeedbacks.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerViewFeedbacks.setAdapter(feedbackAdapter);

        String eventKey = getIntent().getStringExtra("EventKey");

        // Set up the database reference for feedbacks
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        feedbackReference = firebaseDatabase.getReference("feedbacks");

        // Set up the database reference for events
        eventsReference = firebaseDatabase.getReference("events");

        // Set up the database reference for users
        usersReference = firebaseDatabase.getReference("userinfo");

        // Retrieve and display feedback for the specified event
        retrieveAndDisplayFeedback(eventKey);
    }

    private void retrieveAndDisplayFeedback(String eventKey) {
        // Retrieve feedback for the specified event
        feedbackReference.orderByChild("eventKey").equalTo(eventKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        feedbackList.clear();
                        for (DataSnapshot feedbackSnapshot : dataSnapshot.getChildren()) {
                            Feedback feedback = feedbackSnapshot.getValue(Feedback.class);
                            if (feedback != null) {
                                feedbackList.add(feedback);
                            }
                        }
                        feedbackAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle the error
                    }
                });

        // Retrieve event information for the specified event
        eventsReference.child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Assuming Event is a class with appropriate getters
                Event event = dataSnapshot.getValue(Event.class);

                // Update TextViews with event information
                if (event != null) {
                    tvFeedbackListEventName.setText(event.getEventName());

                    // Retrieve user information based on userId
                    String userId = event.getUserId();
                                    tvFeedbackListUsername.setText(userId);
                    if (userId != null) {
                        usersReference.child(userId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                String userName = userSnapshot.getValue(String.class);
                                if (userName != null) {
                                    tvFeedbackListUsername.setText(userName);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle the error
                            }
                        });
                    }

                    tvFeedbackListEventDescription.setText(event.getEventDescription());
                    tvFeedbackListLocationName.setText(event.getEventLocation());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle the error
            }
        });
    }
    private void onNavigationIconClick() {
        Intent intent = new Intent(FeedbackList.this, SideMenuActivity.class);
        startActivity(intent);
    }
}
