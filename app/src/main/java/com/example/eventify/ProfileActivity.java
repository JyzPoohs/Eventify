package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eventify.SideMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    String[] profileItems = {};
    int[] profileIcons = {R.drawable.baseline_account_purple_small_24, R.drawable.baseline_mail_outline_24, R.drawable.baseline_phone_24};
    Button edit_profile_btn;
    DatabaseReference usersReference;

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

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        ListView profileListView = findViewById(R.id.profileListView);

        // Fetch user data from Realtime Database
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
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

                        profileItemsList.add(usernameText);
                        profileItemsList.add(userEmail);
                        profileItemsList.add(contactText);

                        // Convert the ArrayList to an array
                        profileItems = profileItemsList.toArray(new String[0]);
                        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), profileItems, profileIcons);
                        profileListView.setAdapter(customBaseAdapter);
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
}
