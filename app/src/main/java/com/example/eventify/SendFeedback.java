package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendFeedback extends AppCompatActivity {

    TextView TvEvent, TvUsername, TvDescription;
    EditText EtFeedback;
    Button BtnShake;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        TvEvent = findViewById(R.id.tv_feedback_event_name);
        TvUsername = findViewById(R.id.tv_feedback_username);
        TvDescription = findViewById(R.id.tv_feedback_eventDetail);
        EtFeedback = findViewById(R.id.editTextTextMultiLine);
        BtnShake = findViewById(R.id.btn_feedback_shakenow);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        TvEvent.setText(getIntent().getStringExtra("EventName"));
        TvUsername.setText(getIntent().getStringExtra("EventOrganizer"));
        TvDescription.setText(getIntent().getStringExtra("EventDescription"));

        // ...

        BtnShake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the user is authenticated
                if (firebaseAuth.getCurrentUser() != null) {
                    String currentUserUid = firebaseAuth.getCurrentUser().getUid();
                    String eventKey = getIntent().getStringExtra("EventKey");
                    String message = String.valueOf(EtFeedback.getText());

                    // Check for null or empty event key and message
                    if (eventKey != null && !eventKey.isEmpty() && message != null && !message.isEmpty()) {
                        // Generate a unique feedback ID
                        String feedbackID = databaseReference.child("feedbacks").push().getKey();

                        // Create a Feedback object
                        Feedback feedback = new Feedback(message, eventKey, currentUserUid, feedbackID);

                        // Store the feedback in the database
                        databaseReference.child("feedbacks").child(feedbackID).setValue(feedback);

                        // Display a Toast
                        Toast.makeText(SendFeedback.this, "Feedback Submitted Successfully", Toast.LENGTH_SHORT).show();

                        // Start FeedbackListActivity
                        Intent feedbackListIntent = new Intent(SendFeedback.this, FeedbackList.class);
                        feedbackListIntent.putExtra("EventKey", eventKey);

                        try {
                            startActivity(feedbackListIntent);
                        } catch (Exception e) {
                            // Log the error
                            Log.e("SendFeedback", "Error starting FeedbackListActivity", e);
                            // Display a Toast with a brief error message
                            Toast.makeText(SendFeedback.this, "Error starting FeedbackListActivity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the case where event key or message is null or empty
                        Toast.makeText(SendFeedback.this, "Invalid event key or empty feedback message", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the user is not authenticated
                    Toast.makeText(SendFeedback.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });

// ...

    }
}
