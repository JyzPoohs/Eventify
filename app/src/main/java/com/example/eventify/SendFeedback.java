package com.example.eventify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SendFeedback extends AppCompatActivity {

    TextView TvEvent, TvUsername, TvDescription, TvShakeInstruction;
    EditText EtFeedback;
    Button BtnShake, BtnView, BtnPrivate;
    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference usersReference =  usersReference = firebaseDatabase.getReference("userinfo");
    private DatabaseReference eventsReference = eventsReference = firebaseDatabase.getReference("events");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        TvEvent = findViewById(R.id.tv_feedback_event_name);
        TvUsername = findViewById(R.id.tv_feedback_username);
        TvDescription = findViewById(R.id.tv_feedback_eventDetail);
        BtnShake = findViewById(R.id.btn_feedback_shakenow);
        TvShakeInstruction = findViewById(R.id.tv_feedback_message);
        BtnPrivate = findViewById(R.id.btn_private_feedback);
        BtnView = findViewById(R.id.btn_view_feedback);
        EtFeedback = findViewById(R.id.editTextTextMultiLine);


        TvEvent.setText(getIntent().getStringExtra("EventName"));
        TvUsername.setText(getIntent().getStringExtra("EventOrganizer"));
        TvDescription.setText(getIntent().getStringExtra("EventDescription"));

        // ...

        BtnShake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the user is authenticated
                firebaseAuth = FirebaseAuth.getInstance();
                if (firebaseAuth.getCurrentUser() != null) {
                    // Create ShakeDetector instance
                    shakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
                        @Override
                        public void onShake() {
                            // Shaking detected, send the feedback
                            sendFeedback();
                        }
                    });

                    // Get the SensorManager
                    sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                    // Register the ShakeDetector to listen for shakes
                    sensorManager.registerListener(shakeDetector,
                            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                            SensorManager.SENSOR_DELAY_NORMAL);

                    // Display a Toast
                    Toast.makeText(SendFeedback.this, "Shake the phone to send feedback", Toast.LENGTH_SHORT).show();
                    TvShakeInstruction.setText("Shake Now");
                } else {
                    // Handle the case where the user is not authenticated
                    Toast.makeText(SendFeedback.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Your onClick implementation goes here
                String eventKey = getIntent().getStringExtra("EventKey");
                Intent viewFeedbackIntent = new Intent(SendFeedback.this, FeedbackList.class);
                viewFeedbackIntent.putExtra("EventKey", eventKey);
                viewFeedbackIntent.putExtra("ImageUrl", getIntent().getStringExtra("ImageUrl"));
                try {
                    startActivity(viewFeedbackIntent);
                } catch (Exception e) {
                    // Log the error
                    Log.e("SendFeedback", "Error starting FeedbackListActivity", e);
                    // Display a Toast with a brief error message
                    Toast.makeText(SendFeedback.this, "Error starting FeedbackListActivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BtnPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventKey = getIntent().getStringExtra("EventKey");
                String message = String.valueOf(EtFeedback.getText());
                eventsReference.child(eventKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Assuming Event is a class with appropriate getters
                        Event event = dataSnapshot.getValue(Event.class);

                        // Update TextViews with event information
                        if (event != null) {
                            // Retrieve user information based on userId
                            String userId = event.getUserId();
                            if (userId != null) {
                                usersReference.child(userId).child("contact").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot userSnapshot) {
                                        String phoneNumber = userSnapshot.getValue(String.class);
                                        if (phoneNumber != null) {
                                            // Open SMS app with pre-populated message
                                            composeSMS(phoneNumber, message);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Handle the error
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle the error
                    }
                });

            }


        });


    }
    private void sendFeedback(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
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
            feedbackListIntent.putExtra("ImageUrl", getIntent().getStringExtra("ImageUrl"));

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

        if (sensorManager != null && shakeDetector != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }
    private void composeSMS(String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }
    private void onNavigationIconClick() {
        Intent intent = new Intent(SendFeedback.this, SideMenuActivity.class);
        startActivity(intent);
    }
}
