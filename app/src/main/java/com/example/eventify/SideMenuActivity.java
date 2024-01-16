package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SideMenuActivity extends AppCompatActivity {
    String sideMenuItem[] = {"Profile", "Create Event", "Visit Invitations", "Logout"};
    int sideMenuIcon[] = {R.drawable.baseline_account_purple_24, R.drawable.baseline_calendar_month_24, R.drawable.baseline_mail_outline_24,R.drawable.baseline_logout_24};

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
                if (position == 0){
                    Intent intent = new Intent(SideMenuActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
                else if (position == 1){
                    Intent intent = new Intent(SideMenuActivity.this, CreateEventActivity.class);
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