package com.example.eventify;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    String[] profileItems = {};
    int[] profileIcons = {R.drawable.baseline_account_purple_small_24, R.drawable.baseline_mail_outline_24, R.drawable.baseline_phone_24};
    Button edit_profile_btn, delete_profile_btn;
    TextView change_pwd_btn;
    ImageView profilePic;
    DatabaseReference usersReference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        edit_profile_btn = findViewById(R.id.profile_edit_btn);
        progressBar = findViewById(R.id.profile_progress_bar);
        profilePic = findViewById(R.id.profileImageView);
        change_pwd_btn = findViewById(R.id.change_pwd_btn);
        delete_profile_btn = findViewById(R.id.profile_delete_btn);

        change_pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FingerprintActivity.class);
                startActivity(intent);
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        delete_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete your account? This action is irreversible.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed, delete the account
                        deleteAccount();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled, do nothing
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        ListView profileListView = findViewById(R.id.profileListView);

        // Fetch user data from Realtime Database
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            setInProgress(true);
            String userId = currentUser.getUid();
            ArrayList<String> profileItemsList = new ArrayList<>();
            usersReference = FirebaseDatabase.getInstance().getReference("userinfo").child(userId);
            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userEmail = dataSnapshot.child("email").getValue(String.class);
                        String usernameText = dataSnapshot.child("username").getValue(String.class);
                        String contactText = dataSnapshot.child("contact").getValue(String.class);
                        String profilePictureUri = dataSnapshot.child("profile_picture").getValue(String.class);

                        if (profilePictureUri != null && !profilePictureUri.isEmpty()) {
                            Picasso.get().load(profilePictureUri).into(profilePic);
                        } else {
                            profilePic.setImageResource(R.drawable.baseline_account_purple_24);
                        }

                        profileItemsList.add(usernameText);
                        profileItemsList.add(userEmail);
                        profileItemsList.add(contactText);

                        // Convert the ArrayList to an array
                        profileItems = profileItemsList.toArray(new String[0]);
                        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), profileItems, profileIcons);
                        profileListView.setAdapter(customBaseAdapter);

                        setInProgress(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(ProfileActivity.this, SideMenuActivity.class);
        startActivity(intent);
    }

    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Delete the user account
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Account deleted successfully
                                // Now, also remove the user data from the database
                                usersReference.removeValue();

                                Toast.makeText(ProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                // Navigate to the login screen or any other desired screen
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If the deletion fails, display a message to the user
                                Toast.makeText(ProfileActivity.this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
