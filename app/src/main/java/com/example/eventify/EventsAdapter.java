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
        final Event events=this.events.get(position);
        holder.event_name_txt.setText(String.valueOf(events.getEventName()));
        holder.event_datetime_txt.setText(String.valueOf(events.getEventDateTime()));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView event_name_txt,event_datetime_txt;
        public View mainDisplay;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            event_name_txt=itemView.findViewById(R.id.textViewEventName);
            event_datetime_txt=itemView.findViewById(R.id.textViewEventDateTime);

            mainDisplay=itemView;
        }
    }
}
