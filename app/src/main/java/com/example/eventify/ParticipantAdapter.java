package com.example.eventify;

import android.content.Context;
import android.content.Intent;
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

    // Additional fields for eventKey and guestKey
    private String eventKey;
    private String guestKey;

    // Listener for item click
    private ParticipantClickListener clickListener;

    public ParticipantAdapter(Context context, String eventKey, ArrayList<Participants> p) {
        this.context = context;
        this.eventKey = eventKey;
        this.participants = p;
    }

    // Define a listener interface
    public interface ParticipantClickListener {
        void onParticipantClicked(String eventKey, String guestKey, String userID, String username, String contactNumber);
    }

    // Set the listener
    public void setClickListener(ParticipantClickListener listener) {
        this.clickListener = listener;
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
                    Intent intent = new Intent(context, MessageDashboard.class);
                    intent.putExtra("EventKey", eventKey);
                    intent.putExtra("GuestKey", participant.getGuestKey());
                    intent.putExtra("UserID", participant.getUserID());
                    intent.putExtra("Username", participant.getUsername());
                    intent.putExtra("Contact", participant.getContactNumber());
                    context.startActivity(intent);
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
            mainDisplay = itemView;
        }
    }
}
