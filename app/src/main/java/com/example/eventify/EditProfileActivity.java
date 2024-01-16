package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    EditText username, email, contact;
    Button edit_btn;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        username = findViewById(R.id.edit_username);
        email = findViewById(R.id.edit_email);
        contact = findViewById(R.id.edit_contact);
        edit_btn = findViewById(R.id.edit_profile_btn);

        email.setEnabled(false);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            usersReference = FirebaseDatabase.getInstance().getReference("userinfo").child(userId);
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userEmail = dataSnapshot.child("email").getValue(String.class);
                        String usernameText = dataSnapshot.child("username").getValue(String.class);
                        String contactText = dataSnapshot.child("contact").getValue(String.class);

                        email.setText(userEmail);
                        username.setText(usernameText);
                        contact.setText(contactText);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle onCancelled event
                }
            });

            // Update profile when the user clicks the edit button
            edit_btn.setOnClickListener(v -> updateProfile(userId));
        }
    }

    private void updateProfile(String userId) {
        // Get updated data from EditText fields
        String updatedUsername = username.getText().toString().trim();
        String updatedContact = contact.getText().toString().trim();

        // Update the data in the database
        usersReference.child("username").setValue(updatedUsername);
        usersReference.child("contact").setValue(updatedContact);

        Toast.makeText(EditProfileActivity.this, "Edited Successfully", Toast.LENGTH_LONG).show();
    }
}
