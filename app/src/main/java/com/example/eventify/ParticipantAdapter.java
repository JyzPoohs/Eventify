package com.example.eventify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Participants> participants;

    public ParticipantAdapter(Context context, ArrayList<Participants> p) {
        this.context = context;
        this.participants = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewD = inflater.inflate(R.layout.participant_list, parent, false);
        return new MyViewHolder(viewD);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (participants != null && position < participants.size()) {
            final Participants participant = participants.get(position);
            // Set your data to the ViewHolder components
            holder.Username.setText(participant.getUsername());
            holder.ContactNumber.setText(participant.getContactNumber());
            holder.Message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            // Add onClickListener for Delete button
            holder.Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle delete button click
                }
            });
        } else {
            Log.w("ParticipantAdapter", "participants list is null or position out of bounds");
        }


    }

    @Override
    public int getItemCount() {
        return participants.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Username, ContactNumber;
        ImageButton Delete, Message;

        public View mainDisplay;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Username = itemView.findViewById(R.id.Username);
            Delete = itemView.findViewById(R.id.deleteButton);
            Message = itemView.findViewById(R.id.messagebtn);
            ContactNumber = itemView.findViewById(R.id.ContactNum);
            mainDisplay=itemView;

        }
    }
}
