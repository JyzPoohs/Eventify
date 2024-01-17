package com.example.eventify;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventUpdateActivity extends AppCompatActivity {

    private EditText etEventName, etEventDescription, etStart, etEnd;
    private Button btnUpdate;
    private DatabaseReference myRef;
    private String id, eventName, eventDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_update);

        etEventName = findViewById(R.id.etEventName);
        etEventDescription = findViewById(R.id.etEventDescription);
        etStart = findViewById(R.id.etStart);
        etEnd = findViewById(R.id.etEnd);
        btnUpdate = findViewById(R.id.btnUpdate);

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


        if (!updatedEventName.isEmpty() && !updatedEventDescription.isEmpty() && !updatedEventStart.isEmpty() && !updatedEventEnd.isEmpty()) {

            myRef.child("eventName").setValue(updatedEventName);
            myRef.child("eventDescription").setValue(updatedEventDescription);
            myRef.child("eventStart").setValue(updatedEventStart);
            myRef.child("eventEnd").setValue(updatedEventEnd);

            // Redirect back to EventDetailActivity
            Intent intent = new Intent(EventUpdateActivity.this, EventListActivity.class);
            intent.putExtra("EventKey", id);
            startActivity(intent);
            finish();
        }
    }
}
