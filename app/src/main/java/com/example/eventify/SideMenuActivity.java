package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class SideMenuActivity extends AppCompatActivity {
    String sideMenuItem[] = {"Home", "Profile", "Create Event", "View Event", "Visit Invitations", "Logout"};
    int sideMenuIcon[] = {R.drawable.baseline_home_24, R.drawable.baseline_account_purple_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_event_24, R.drawable.baseline_mail_outline_24, R.drawable.baseline_logout_24};

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        listView = findViewById(R.id.sideMenu_list);

        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), sideMenuItem, sideMenuIcon);
        listView.setAdapter(customBaseAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(SideMenuActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(SideMenuActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(SideMenuActivity.this, CreateEventActivity.class);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(SideMenuActivity.this, EventListActivity.class);
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(SideMenuActivity.this, Invitation.class);
                    startActivity(intent);
                } else if (position == 5) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(SideMenuActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
                    startActivity(intent);
                }
            }
        });
    }

    private void onNavigationIconClick() {
        Intent intent = new Intent(SideMenuActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}