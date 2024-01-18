package com.example.eventify;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Invitation extends AppCompatActivity {


    Button AddGuestbtn;
    ListView UserList;
    ArrayAdapter<String> userListAdapter;
    DatabaseReference usersReference;


    FirebaseAuth firebaseAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);


        UserList = findViewById(R.id.UserList);
        AddGuestbtn = findViewById(R.id.AddBtn);


        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference().child("userinfo");
        firebaseAuth = FirebaseAuth.getInstance();


        // Fetch users from Firebase and set up the adapter
        fetchUsers();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onNavigationIconClick());
        // Handle the "Add Guest" button click
        AddGuestbtn.setOnClickListener(view -> {
            // Get the currently logged-in user
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {

                // Iterate through the user list to get selected users
                List<String> selectedUsers = new ArrayList<>();
                for (int i = 0; i < userListAdapter.getCount(); i++) {
                    View item = UserList.getChildAt(i);
                    CheckBox checkBox = item.findViewById(R.id.checkBox);

                    if (checkBox.isChecked()) {
                        String selectedUserName = userListAdapter.getItem(i);
                        Log.d("Guest", "Position: " + i + ", Selected username: " + selectedUserName);
                        selectedUsers.add(selectedUserName);
                    }
                }

                // Do something with the selected users (e.g., add them to the event)

                addGuestsToEvent(selectedUsers);



            } else {
                // User is not logged in, handle accordingly
                Toast.makeText(Invitation.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchUsers() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String currentUserID = currentUser.getUid();
                    String currentUserUsername = currentUser.getDisplayName();
                    List<String> userList = new ArrayList<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        String username = userSnapshot.child("username").getValue(String.class);
                        if (userId != null && username != null) {
                            if (!userId.equals(currentUserID) && !username.equals(currentUserUsername)) {
                                userList.add(username);  // Add usernames to the list
                            }
                        }
                    }
                    userListAdapter = new ArrayAdapter<String>(Invitation.this, R.layout.guest_list_item, userList) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                            View view = convertView;

                            if (view == null) {
                                // Inflate a new view if convertView is null
                                view = getLayoutInflater().inflate(R.layout.guest_list_item, parent, false);
                            }
                            // Get the username at the current position
                            String username = getItem(position);
                            // Set the username to the TextView
                            TextView usernameTextView = view.findViewById(R.id.usernameTextView);
                            usernameTextView.setText(username);
                            // Set up the CheckBox
                            CheckBox checkBox = view.findViewById(R.id.checkBox);
                            checkBox.setChecked(UserList.isItemChecked(position));
                            return view;
                        }
                    };
                    UserList.setAdapter(userListAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(Invitation.this, "Error fetching users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void onNavigationIconClick() {
        Intent intent = new Intent(Invitation.this, SideMenuActivity.class);
        startActivity(intent);
    }
    private void addGuestsToEvent(List<String> guestUserNames) {
        // Get the event ID from the intent
        String eventId = getIntent().getStringExtra("EventKey");
        DatabaseReference invitationsReference = FirebaseDatabase.getInstance().getReference("invitations");

        // Generate a unique key for the new invitation
        String invitationId = invitationsReference.push().getKey();

        // Create a map to store invitation details
        Map<String, Object> invitationMap = new HashMap<>();
        invitationMap.put("eventId", eventId);
        invitationMap.put("guestUserNames", guestUserNames);

        // Set the invitation details under the unique invitation ID
        invitationsReference.child(invitationId).setValue(invitationMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Invitation.this, "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Invitation.this, "Failed to send invitation: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Log.d("Invitation", "Event ID: " + eventId);
        if (eventId != null) {
            // Get a reference to the event node in the database
            DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference("events").child(eventId).child("guests");


            // Add each guest user ID to the map with a value of true
            for (String guestUserName : guestUserNames) {
                eventReference.child(guestUserName).setValue(true);
                Log.d("Invitation", "Added guest: " + guestUserName);
            }
            Toast.makeText(Invitation.this, "Guests added to the event", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(Invitation.this, "Event ID not found", Toast.LENGTH_SHORT).show();
        }

        }

}
