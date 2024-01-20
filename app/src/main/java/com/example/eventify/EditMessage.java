package com.example.eventify;


import androidx.annotation.NonNull;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class EditMessage extends AppCompatActivity {

    Button Edit,PlayBtn,PlayBtn2;
    ImageButton SoundRecord;
    EditText textMessage;
    TextView name1,audioStatus;

    private MediaRecorder mediaRecorder;
    private String audioFilePath = null;
    private boolean isRecording = false;
    private MediaPlayer mediaPlayer;

    private static final int RECORD_AUDIO_REQUEST_CODE = 1;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
        storageReference = FirebaseStorage.getInstance().getReference();

        Edit=findViewById(R.id.EditBtn);
        PlayBtn=findViewById(R.id.Playbtn);
        SoundRecord=findViewById(R.id.soundRecord1);
        textMessage=findViewById(R.id.textMessage1);
        name1=findViewById(R.id.name1);
        audioStatus=findViewById(R.id.audioStatus1);
        PlayBtn2=findViewById(R.id.Playbtn2);
        String guestKey = getIntent().getStringExtra("GuestKey");
        String username = getIntent().getStringExtra("Username");
        String userId = getIntent().getStringExtra("UserID");
        String eventId = getIntent().getStringExtra("EventKey");
        String contact = getIntent().getStringExtra("Contact");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        File externalFilesDir = getExternalFilesDir(null);
        audioFilePath = externalFilesDir.getAbsolutePath() + File.separator + "audio.3gp";
        checkAndRequestPermissions();
        DatabaseReference voiceMessageRef = FirebaseDatabase.getInstance().getReference()
                .child("events").child(eventId).child("guests").child(guestKey).child("audioKey");

        voiceMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String voiceMessageUrl = dataSnapshot.getValue(String.class);
                // Set the URL to the MediaPlayer for playback
                setMediaPlayerDataSource(voiceMessageUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        DatabaseReference textMessageRef = FirebaseDatabase.getInstance().getReference()
                .child("events").child(eventId).child("guests").child(guestKey).child("message");

        textMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String previousTextMessage = dataSnapshot.getValue(String.class);
                // Set the previous text message to the EditText
                textMessage.setText(previousTextMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        name1.setText(username);

        mediaPlayer = new MediaPlayer();
        PlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start playing the voice message
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
            }
        });
        SoundRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });
        PlayBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start playing the recorded audio file
                try {
                    if (mediaPlayer != null) {
                        // Ensure the MediaPlayer is in the stopped state before starting playback
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(audioFilePath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        audioStatus.setText("Playing recorded sound...");

                        // Set a completion listener to release the MediaPlayer resources after playback is complete
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioStatus.setText("Playback completed");
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Media", "Failed to play recorded audio: " + e.getMessage());
                    Toast.makeText(EditMessage.this, "Failed to play recorded audio", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the new text from the EditText
                String newText = textMessage.getText().toString().trim();

                // Update the text message in the events database
                DatabaseReference newTextMessageRef = FirebaseDatabase.getInstance().getReference()
                        .child("events").child(eventId).child("guests").child(userId).child("message");

                newTextMessageRef.setValue(newText)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Message", "Text message updated successfully");
                                Toast.makeText(EditMessage.this, "Text message updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Message", "Failed to update text message: " + e.getMessage());
                                Toast.makeText(EditMessage.this, "Failed to update text message", Toast.LENGTH_SHORT).show();
                            }
                        });
                DatabaseReference invitationsRef = FirebaseDatabase.getInstance().getReference().child("invitations");
                uploadNewAudioFile(guestKey, userId, eventId);
                invitationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String guestKeyFromSnapshot = childSnapshot.getKey();
                            DatabaseReference currentGuestRef = invitationsRef.child(guestKeyFromSnapshot).child("guests").child(userId);
                            currentGuestRef.child("message").setValue(newText);
                            break;
                        }
                        Toast.makeText(EditMessage.this, "Text & Voice Text Updated successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }

                });


            }
        });
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
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, RECORD_AUDIO_REQUEST_CODE);
            }
        }
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

    private void uploadNewAudioFile(String guestKey, String userId, String eventId) {
        if (audioFilePath != null) {
            Uri audioFileUri = Uri.fromFile(new File(audioFilePath));
            String audioFileName = "audio_" + System.currentTimeMillis() + ".3gp";
            StorageReference audioRef = storageReference.child("audio").child(audioFileName);
            audioRef.putFile(audioFileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String newAudioDownloadUrl = uri.toString();

                            // Update the database with the new audio URL
                            DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference()
                                    .child("events").child(eventId).child("guests").child(guestKey);
                            eventsRef.child("audioKey").setValue(newAudioDownloadUrl);

                            // Update 'invitations' node with the new audio URL
                            DatabaseReference invitationsRef = FirebaseDatabase.getInstance().getReference().child("invitations");
                            invitationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        String guestKeyFromSnapshot = childSnapshot.getKey();
                                        DatabaseReference currentGuestRef = invitationsRef.child(guestKeyFromSnapshot).child("guests").child(userId);
                                        currentGuestRef.child("voiceMessage").setValue(newAudioDownloadUrl);
                                        break;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle error
                                }
                            });

                            // Add any additional logic or error handling as needed
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditMessage.this, "Failed to upload new audio", Toast.LENGTH_SHORT).show();
                    });
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

    private void onNavigationIconClick() {
        Intent intent = new Intent(EditMessage.this, SideMenuActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }
}