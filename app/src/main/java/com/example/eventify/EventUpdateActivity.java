package com.example.eventify;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventUpdateActivity extends AppCompatActivity {

    private EditText etEventName, etEventDescription, etStart, etEnd, etLocation;
    private Button btnUpdate;
    private ImageButton btnCapture;
    private ImageView imageLocation;
    private DatabaseReference myRef;
    private String id, eventName, eventDescription;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_update);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        etEventName = findViewById(R.id.etEventName);
        etEventDescription = findViewById(R.id.etEventDescription);
        etStart = findViewById(R.id.etStart);
        etEnd = findViewById(R.id.etEnd);
        etLocation = findViewById(R.id.etLocation);
        imageLocation = findViewById(R.id.imageLocation);
        btnCapture = findViewById(R.id.btnCapture);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        // Get the event ID and other details from the Intent
        if (getIntent().hasExtra("EventKey")) {
            id = getIntent().getStringExtra("EventKey");
            eventName = getIntent().getStringExtra("EventName");
            eventDescription = getIntent().getStringExtra("EventDescription");

        } else {
            finish();
        }

        myRef = FirebaseDatabase.getInstance().getReference().child("events").child(id);

        // Load existing event details for editing
        loadEventDetails();

        // Set click listeners for date and time pickers
        etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(etStart);
            }
        });

        etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(etEnd);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEventDetails();
            }
        });
    }

    private void loadEventDetails() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if (event != null) {
                        etEventName.setText(event.getEventName());
                        etEventDescription.setText(event.getEventDescription());
                        etStart.setText(event.getEventStart());
                        etEnd.setText(event.getEventEnd());
                        etLocation.setText(event.getEventLocation());

                        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                            Glide.with(EventUpdateActivity.this)
                                    .load(event.getImageUrl())
                                    .into(imageLocation);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDateTimePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                EventUpdateActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);

                                        // Format the selected date and time with day of the week
                                        String dateFormat = "yyyy-MM-dd (EEEE) HH:mm";
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
                                        editText.setText(simpleDateFormat.format(calendar.getTime()));
                                    }
                                },
                                hour,
                                minute,
                                true
                        );
                        timePickerDialog.show();
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void updateEventDetails() {
        String updatedEventName = etEventName.getText().toString().trim();
        String updatedEventDescription = etEventDescription.getText().toString().trim();
        String updatedEventStart = etStart.getText().toString().trim();
        String updatedEventEnd = etEnd.getText().toString().trim();
        String updatedEventLocation = etLocation.getText().toString().trim();

        if (!updatedEventName.isEmpty() && !updatedEventDescription.isEmpty() && !updatedEventStart.isEmpty() && !updatedEventEnd.isEmpty()) {

            myRef.child("eventName").setValue(updatedEventName);
            myRef.child("eventDescription").setValue(updatedEventDescription);
            myRef.child("eventStart").setValue(updatedEventStart);
            myRef.child("eventEnd").setValue(updatedEventEnd);
            myRef.child("eventLocation").setValue(updatedEventLocation);

            if (imageUri != null) {
                // Upload the new image to storage and update the image URL
                uploadImageToStorage(imageUri);
            } else {
                // Continue with updating the event details in the database if no new image is selected
                navigateToEventListActivity();
            }
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        // Get a reference to the Firebase Storage root, and then create a reference to the image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("event_images");
        StorageReference imageRef = storageRef.child(id + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image upload success, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Continue with updating the event details in the database
                        updateEventDetails(uri);
                    });
                })
                .addOnFailureListener(e -> {
                    // Image upload failed, handle the error
                    // You may want to display a message to the user
                    Toast.makeText(EventUpdateActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateEventDetails(Uri downloadUri) {
        String updatedEventName = etEventName.getText().toString().trim();
        String updatedEventDescription = etEventDescription.getText().toString().trim();
        String updatedEventStart = etStart.getText().toString().trim();
        String updatedEventEnd = etEnd.getText().toString().trim();
        String updatedEventLocation = etLocation.getText().toString().trim();

        if (!updatedEventName.isEmpty() && !updatedEventDescription.isEmpty() && !updatedEventStart.isEmpty() && !updatedEventEnd.isEmpty()) {
            // Delete the existing image in Firebase Storage
            deleteExistingImage();

            myRef.child("eventName").setValue(updatedEventName);
            myRef.child("eventDescription").setValue(updatedEventDescription);
            myRef.child("eventStart").setValue(updatedEventStart);
            myRef.child("eventEnd").setValue(updatedEventEnd);
            myRef.child("eventLocation").setValue(updatedEventLocation);
            myRef.child("imageUrl").setValue(downloadUri.toString());

            // Continue with updating the event details in the database
            navigateToEventListActivity();
        }
    }

    private void deleteExistingImage() {
        // Retrieve the existing image URL from Firebase Database
        myRef.child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingImageUrl = dataSnapshot.getValue(String.class);

                    // Delete the existing image in Firebase Storage
                    if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                        StorageReference existingImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(existingImageUrl);
                        existingImageRef.delete().addOnSuccessListener(aVoid -> {
                            // Image deletion success
                        }).addOnFailureListener(e -> {
                            // Image deletion failed, handle the error
                            // You may want to display a message to the user
                            Toast.makeText(EventUpdateActivity.this, "Failed to delete existing image", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    private void navigateToEventListActivity() {
        // Redirect back to EventListActivity
        Intent intent = new Intent(EventUpdateActivity.this, EventListActivity.class);
        intent.putExtra("EventKey", id);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageLocation.setImageBitmap(imageBitmap);

                // Get the URI of the captured image
                imageUri = getImageUri(getApplicationContext(), imageBitmap);
            } else {
                // Handle the case where extras is null (user canceled image capture)
                // You may want to show a message to the user.
                Toast.makeText(this, "Image capture canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(EventUpdateActivity.this, SideMenuActivity.class);
        startActivity(intent);
    }
}