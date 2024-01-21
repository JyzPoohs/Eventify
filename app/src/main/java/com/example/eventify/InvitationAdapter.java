package com.example.eventify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<EventInvitation> EventInvitation;

    public InvitationAdapter(Context context, ArrayList<EventInvitation> in) {
        this.context = context;
        this.EventInvitation = in;
    }

    @NonNull
    @Override
    public InvitationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewD=inflater.inflate(R.layout.invitationeventlist,parent,false);
        return new InvitationAdapter.MyViewHolder(viewD);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationAdapter.MyViewHolder holder, final int position) {
        final EventInvitation eventInvitation = this.EventInvitation.get(position);
        holder.event_name_txt.setText(eventInvitation.getEventName());
        holder.event_organizer_txt.setText(eventInvitation.getEventOrganizer());
        holder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventInvitationDetails.class);
                intent.putExtra("EventKey", eventInvitation.getEventKey());
                intent.putExtra("User ID", eventInvitation.getUserId());
                intent.putExtra("EventName", eventInvitation.getEventName() );
                intent.putExtra("Username", eventInvitation.getUsername());
                intent.putExtra("EventDescription", eventInvitation.getEventDescription());
                intent.putExtra("EventStart",eventInvitation.getEventStart());
                intent.putExtra("EventEnd",eventInvitation.getEventEnd());
                intent.putExtra("EventLocation",eventInvitation.getEventLocation());
                intent.putExtra("ImageUrl",eventInvitation.getEventLocationImg());
                intent.putExtra("EventOrganizer",eventInvitation.getEventOrganizer());
                intent.putExtra("TextMessage",eventInvitation.getTextMessage());
                intent.putExtra("VoiceMessage",eventInvitation.getVoiceMessage());
                intent.putExtra("EventType",eventInvitation.getEventType());
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return EventInvitation.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView event_name_txt ,event_organizer_txt;
        Button viewBtn;
        public View mainDisplay;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            event_name_txt=itemView.findViewById(R.id.EventName);
            event_organizer_txt=itemView.findViewById(R.id.EventOrganizer);
            viewBtn=itemView.findViewById(R.id.ViewBtn);
            mainDisplay=itemView;
        }
    }
}
