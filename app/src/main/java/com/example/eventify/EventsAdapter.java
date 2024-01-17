package com.example.eventify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Event> events;

    public EventsAdapter(Context context, ArrayList<Event> p) {
        this.context = context;
        this.events = p;
    }

    @NonNull
    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewD=inflater.inflate(R.layout.event_list,parent,false);
        return new MyViewHolder(viewD);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Event event = this.events.get(position);
        holder.event_name_txt.setText(String.valueOf(event.getEventName()));
        holder.mainDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("EventKey",event.getEventKey());
                intent.putExtra("EventName", event.getEventName());
                intent.putExtra("Start", event.getEventStart());
                intent.putExtra("End", event.getEventEnd());
                intent.putExtra("EventDescription", event.getEventDescription());
                intent.putExtra("EventLocation", event.getEventLocation());
                intent.putExtra("ImageUrl", event.getImageUrl());
                // Add more fields as needed
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView event_name_txt;
        public View mainDisplay;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            event_name_txt=itemView.findViewById(R.id.textViewEventName);

            mainDisplay=itemView;
        }
    }
}
