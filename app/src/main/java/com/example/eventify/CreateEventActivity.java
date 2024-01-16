package com.example.eventify;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private TextView dateTime;
    private EditText etEventName;
    private EditText etEventDescription;
    private EditText etEventTheme;
    private EditText etEventLocation;
    private Button btnCreate;
    private ImageButton btnSelectDateTime;
    private ImageButton btnCapturePhoto;
    private ImageView imageLocation;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invitation);

        etEventName = findViewById(R.id.eventName);
        etEventDescription = findViewById(R.id.eventDescription);
        etEventTheme = findViewById(R.id.eventTheme);
        etEventLocation = findViewById(R.id.Location);
        dateTime = findViewById(R.id.dateTime);
        btnSelectDateTime = findViewById(R.id.btnSelectDateTime);
        imageLocation = findViewById(R.id.imageLocation);
        btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        btnCreate = findViewById(R.id.btnCreate);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("events");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("event_images");
        btnSelectDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = etEventName.getText().toString().trim();
                String eventDescription = etEventDescription.getText().toString().trim();
                String eventTheme = etEventTheme.getText().toString().trim();
                String eventLocation = etEventLocation.getText().toString().trim();
                String eventDateTime = dateTime.getText().toString().trim();

                // Check if all fields are filled
                if (!eventName.isEmpty() && !eventDescription.isEmpty() && !eventTheme.isEmpty() &&
                        !eventLocation.isEmpty() && !eventDateTime.isEmpty()) {

                    // Generate a unique key for the event
                    String eventKey = databaseReference.child("events").push().getKey();

                    // Store event data to Firebase Realtime Database
                    storeEventData(eventKey, eventName, eventDescription, eventTheme, eventLocation, eventDateTime);

                    // Capture the image as a Bitmap from ImageView
                    Bitmap imageBitmap = ((BitmapDrawable) imageLocation.getDrawable()).getBitmap();

                    // Upload the image to Firebase Storage
                    uploadImageToStorage(eventKey, imageBitmap);

                    Intent intent = new Intent(CreateEventActivity.this,EventDetailActivity.class);
                    startActivity(intent);
                } else {
                    // Display a message or handle incomplete data
                    // You may show a Toast or set an error message on the UI
                }
            }
        });


    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        showTimePicker(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker(final String selectedDate) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = hourOfDay + ":" + minute;
                        dateTime.setText(selectedDate + " " + selectedTime);
                    }
                },
                hour, minute, true);

        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageLocation.setImageBitmap(imageBitmap);
        }
    }

    private void storeEventData(String eventKey, String eventName, String eventDescription, String eventTheme, String eventLocation, String eventDateTime) {
        // Create an Event object
        Event event = new Event(eventName, eventDescription, eventTheme, eventLocation, eventDateTime);

        // Store the event in the database
        databaseReference.child("events").child(eventKey).setValue(event);
    }

    private void uploadImageToStorage(String eventKey, Bitmap bitmap) {
        // Create a StorageReference for the image
        StorageReference imageRef = storageReference.child("event_images").child(eventKey + ".jpg");

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Upload the byte array to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(imageData);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image uploaded successfully, get the download URL
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Store the download URL in Firebase Realtime Database
                    storeEventImage(eventKey, uri.toString());
                });
            } else {
                // Handle unsuccessful upload
            }
        });
    }

    private void storeEventImage(String eventKey, String imageUrl) {
        // Create an Event object with the image URL
        Event event = new Event(imageUrl);

        // Store the event in the database
        databaseReference.child("events").child(eventKey).child("imageUrl").setValue(event.getImageUrl());
    }

}