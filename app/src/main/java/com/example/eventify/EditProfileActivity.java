package com.example.eventify;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    ImageView profilePic;
    EditText username, email, contact;
    Button edit_btn;
    DatabaseReference usersReference;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        username = findViewById(R.id.edit_username);
        email = findViewById(R.id.edit_email);
        contact = findViewById(R.id.edit_contact);
        edit_btn = findViewById(R.id.edit_profile_btn);
        profilePic = findViewById(R.id.editProfileImageView);

        email.setEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });


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
                        String profilePictureUri = dataSnapshot.child("profile_picture").getValue(String.class);

                        email.setText(userEmail);
                        username.setText(usernameText);
                        contact.setText(contactText);
                        if (profilePictureUri != null && !profilePictureUri.isEmpty()) {
                            Picasso.get().load(profilePictureUri).into(profilePic);
                        }else
                            profilePic.setImageResource(R.drawable.baseline_account_purple_24);

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

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                selectedImageUri = uri;
                profilePic.setImageURI(selectedImageUri);
                Log.d("PhotoPicker", "Selected URI: " + selectedImageUri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });


    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(EditProfileActivity.this, SideMenuActivity.class);
        startActivity(intent);
    }

    private void updateProfile(String userId) {
        // Get updated data from EditText fields
        String updatedUsername = username.getText().toString().trim();
        String updatedContact = contact.getText().toString().trim();

        // Update the data in the database
        usersReference.child("username").setValue(updatedUsername);
        usersReference.child("contact").setValue(updatedContact);
        saveImageToStorage(selectedImageUri);
        updateProfilePicture(userId, selectedImageUri.toString());

        Toast.makeText(EditProfileActivity.this, "Edited Successfully", Toast.LENGTH_LONG).show();
    }

    private void saveImageToStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_pictures");
        String imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageName);

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    Log.d("PhotoPicker", "Image uploaded successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful image upload
                    Log.e("PhotoPicker", "Error uploading image", e);
                });
    }

    // Update the profile picture URL in the Realtime Database
    private void updateProfilePicture(String userId, String imageUrl) {
        // Update the profile picture URL in the "profile_picture" node
        usersReference.child("profile_picture").setValue(imageUrl);
    }
}
