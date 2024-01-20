package com.example.eventify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.home_recycle_view);

        List<HomeItem> items = new ArrayList<HomeItem>();
        items.add(new HomeItem("Create Event",R.drawable.baseline_calendar_month_24));
        items.add(new HomeItem("View Event",R.drawable.baseline_event_80));
        items.add(new HomeItem("View Invitations",R.drawable.baseline_mail_outline_24));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(getApplicationContext(),items));

        MyAdapter adapter = new MyAdapter(getApplicationContext(), items);
        recyclerView.setAdapter(adapter);

        // Set the item click listener
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // Code to navigate to CreateEventActivity
                Intent createEventIntent = new Intent(HomeActivity.this, CreateEventActivity.class);
                startActivity(createEventIntent);
                break;
            case 1:
                // Code to navigate to EventListActivity
                Intent viewEventIntent = new Intent(HomeActivity.this, EventListActivity.class);
                startActivity(viewEventIntent);
                break;
            case 2:
                // Code to navigate to EventListActivity
                Intent viewInvitationsIntent = new Intent(HomeActivity.this, Invitation.class);
                startActivity(viewInvitationsIntent);
                break;
        }
    }

    public void onNavigationIconClick() {
        Intent intent = new Intent(HomeActivity.this, SideMenuActivity.class);
        startActivity(intent);
    };

}