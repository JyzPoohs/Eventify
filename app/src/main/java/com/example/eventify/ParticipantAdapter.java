package com.example.eventify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
                    showConfirmationDialog(eventKey,participant.getGuestKey(),participant.getUserID());
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
    private void deleteParticipant(String eventKey,String guestKey) {
        // Check if the participantsRef is set

            // Remove the participant from the database
            DatabaseReference participantsRef=FirebaseDatabase.getInstance().getReference();
            participantsRef.child("events").child(eventKey).child("guests").child(guestKey).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Participant deleted successfully
                            // You may want to update the UI or show a message here
                            notifyDataSetChanged(); // Notify the adapter that the data set has changed
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to delete participant
                            // You may want to handle the failure, e.g., show an error message
                        }
                    });
        }
    private void showConfirmationDialog(final String eventKey,final String guestKey, final String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this participant?");

        // Positive button (Yes action)
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked Yes, delete the participant
                deleteParticipant(eventKey,guestKey);
                delete(eventKey,userId);
            }
        });

        // Negative button (No action)
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked No, do nothing or dismiss the dialog
                dialogInterface.dismiss();
            }
        });

        // Create and show the dialog
        builder.create().show();
    }
    private void delete(String eventKey,String userId) {
        // Remove the participant from the database
        DatabaseReference InvitationsRef=FirebaseDatabase.getInstance().getReference();
        InvitationsRef.child("invitations").child(eventKey).child("guests").child(userId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Participant deleted successfully
                        // You may want to update the UI or show a message here
                        notifyDataSetChanged(); // Notify the adapter that the data set has changed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete participant
                        // You may want to handle the failure, e.g., show an error message
                    }
                });
    }

}
