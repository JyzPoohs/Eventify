package com.example.eventify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class SendMessage extends AppCompatActivity {
    ImageButton call, voiceRecord;
    Button sendMessage;
    TextView userName, phoneNum, audioStatus;
    EditText textMessage;

    private static final int RECORD_AUDIO_REQUEST_CODE = 1;

    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String audioFilePath = null;
    private boolean isRecording = false;

    // Firebase Storage
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        userName = findViewById(R.id.name1);
        phoneNum = findViewById(R.id.phoneNum);
        textMessage = findViewById(R.id.textMessage1);
        sendMessage = findViewById(R.id.EditBtn);
        voiceRecord = findViewById(R.id.soundRecord1);
        call = findViewById(R.id.callBtn);
        audioStatus = findViewById(R.id.audioStatus);
        Button playButton = findViewById(R.id.PlayBtn);

        String guestKey = getIntent().getStringExtra("GuestKey");
        String username = getIntent().getStringExtra("Username");
        String userId = getIntent().getStringExtra("UserID");
        String eventId = getIntent().getStringExtra("EventKey");
        String contact = getIntent().getStringExtra("Contact");
        userName.setText(username);
        phoneNum.setText(contact);

        File externalFilesDir = getExternalFilesDir(null);
        audioFilePath = externalFilesDir.getAbsolutePath() + File.separator + "audio.3gp";
        // Check and request permissions if needed
        checkAndRequestPermissions();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textMessage.getText().toString().trim();

                if (!message.isEmpty()) {
                    // Update 'invitations' node
                    DatabaseReference invitationsRef = FirebaseDatabase.getInstance().getReference().child("invitations");
                    invitationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                String guestKeyFromSnapshot = childSnapshot.getKey();
                                DatabaseReference currentGuestRef = invitationsRef.child(guestKeyFromSnapshot).child("guests").child(userId);
                                currentGuestRef.child("message").setValue(message);
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle error
                        }
                    });

                    // Update 'events' node
                    DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventId).child("guests").child(guestKey);
                    eventsRef.child("message").setValue(message);

                    // Optionally, you can add a timestamp or any other relevant information
                    // eventsRef.child("timestamp").setValue(ServerValue.TIMESTAMP);

                    // Upload audio file to Firebase Storage
                    uploadAudioFile(guestKey, userId, eventId);

                    // Add any additional logic or error handling as needed
                } else {
                    Toast.makeText(SendMessage.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        voiceRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start playing the audio file
                try {
                    mediaPlayer.setDataSource(audioFilePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Media", "Failed to play audio: " + e.getMessage());
                    Toast.makeText(SendMessage.this, "Failed to play audio", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Update UI or do something when playback is complete
                playButton.setEnabled(true);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, RECORD_AUDIO_REQUEST_CODE);
            }
        }
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            audioStatus.setText("Recording...");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Media", "Failed to start recording: " + e.getMessage());
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        audioStatus.setText("Recording stopped");
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now start recording
                startRecording();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadAudioFile(String guestKey, String userId, String eventId) {
        if (audioFilePath != null) {
            Uri audioFileUri = Uri.fromFile(new File(audioFilePath));
            String audioFileName = "audio_" + System.currentTimeMillis() + ".3gp";
            StorageReference audioRef = storageReference.child("audio").child(audioFileName);
            audioRef.putFile(audioFileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String audioDownloadUrl = uri.toString();
                            // Now you can save the audioDownloadUrl to the database
                            // and associate it with the corresponding message or user.

                            DatabaseReference invitationsRef = FirebaseDatabase.getInstance().getReference().child("invitations");
                            invitationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        String guestKeyFromSnapshot = childSnapshot.getKey();
                                        DatabaseReference currentGuestRef = invitationsRef.child(guestKeyFromSnapshot).child("guests").child(userId);
                                        currentGuestRef.child("voiceMessage").setValue(audioDownloadUrl);
                                        break;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                }
                            });

                            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("events").child(eventId).child("guests").child(guestKey);
                            // Update 'events' node with audio URL
                            eventsRef.child("audioKey").setValue(audioDownloadUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SendMessage.this, "Failed to upload audio", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(SendMessage.this, SideMenuActivity.class);
        startActivity(intent);
    }
}
