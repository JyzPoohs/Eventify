package com.example.eventify;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private List<Feedback> feedbackList;

    // Constructor to receive the data
    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    public void updateData(List<Feedback> newFeedbackList) {
        feedbackList = newFeedbackList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        // Set data to the views in the ViewHolder
        if (feedback != null) {
            String userId = feedback.getUserId();
            holder.tvFeedbackMessage.setText(feedback.getMessage());

            // Get the username from Firebase using userId
            if (userId != null) {
                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("userinfo");
                usersReference.child(userId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.getValue(String.class);
                        if (username != null) {
                            // Set the username to the TextView
                            holder.tvFeedbackUsername.setText(username);

                            // Set visibility of edit and delete buttons based on conditions
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String currentUserId = currentUser.getUid(); // Replace with your method to get the current user ID
                            String feedbackOwnerId = feedback.getUserId();
                            boolean isEventOwner = isEventOwner(feedback.getEventKey()); // Replace with your method to check if the current user is the event owner

                            if (currentUserId != null && currentUserId.equals(feedbackOwnerId)) {
                                // If the comment belongs to the current user, show edit and delete buttons
                                holder.btnEditFeedback.setVisibility(View.VISIBLE);
                                holder.btnDeleteFeedback.setVisibility(View.VISIBLE);
                                holder.setDeleteButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Call a method to delete the feedback
                                        deleteFeedback(feedback);
                                    }
                                });
                            } else if (isEventOwner) {
                                // If the event belongs to the current user, show delete button
                                holder.btnDeleteFeedback.setVisibility(View.VISIBLE);
                                holder.setDeleteButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Call a method to delete the feedback
                                        deleteFeedback(feedback);
                                    }
                                });
                            }

                            // Set edit button click listener
                            holder.setEditButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Call a method to show the edit dialog
                                    showEditDialog(feedback, holder);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle the error
                    }
                });

                // Retrieve the event ID from the feedback
                String eventId = feedback.getEventKey();
                if (eventId != null) {
                    // Query the database to get the event information
                    DatabaseReference eventsReference = FirebaseDatabase.getInstance().getReference("events");
                    eventsReference.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot eventSnapshot) {
                            if (eventSnapshot.exists()) {
                                // Assuming Event is a class with appropriate getters
                                Event event = eventSnapshot.getValue(Event.class);

                                if (event != null) {
                                    // Now you have the event information, including the user ID
                                    String eventUserId = event.getUserId();
                                    // Use eventUserId as needed
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle the error
                        }
                    });
                }
            }
            // Set other views as needed
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    // ViewHolder class to hold the views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFeedbackUsername;
        TextView tvFeedbackMessage;
        ImageButton btnEditFeedback;
        ImageButton btnDeleteFeedback;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeedbackUsername = itemView.findViewById(R.id.tvFeedbackUsername);
            tvFeedbackMessage = itemView.findViewById(R.id.tvFeedbackMessage);
            btnEditFeedback = itemView.findViewById(R.id.btnEditFeedback);
            btnDeleteFeedback = itemView.findViewById(R.id.btnDeleteFeedback);
            // Initialize other views if needed
        }

        public void setDeleteButtonClickListener(View.OnClickListener listener) {
            btnDeleteFeedback.setOnClickListener(listener);
        }

        public void setEditButtonClickListener(View.OnClickListener listener) {
            btnEditFeedback.setOnClickListener(listener);
        }
    }

    // Define your isEventOwner method here
    private boolean isEventOwner(String eventId) {
        // Implement your logic to check if the current user is the event owner
        // Return true if the current user is the event owner, otherwise return false
        return false;
    }

    private void deleteFeedback(Feedback feedback) {
        // Delete the feedback from the database
        DatabaseReference feedbackReference = FirebaseDatabase.getInstance().getReference("feedbacks");
        feedbackReference.child(feedback.getFeedbackId()).removeValue();
        // Update the data and notify the adapter
        List<Feedback> updatedList = new ArrayList<>(feedbackList);
        updatedList.remove(feedback);
        updateData(updatedList);
    }

    private void showEditDialog(Feedback feedback, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        View dialogView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.edit_feedback_dialog, null);
        builder.setView(dialogView);

        EditText editTextFeedback = dialogView.findViewById(R.id.editTextFeedback);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Set the existing feedback message in the edit text
        editTextFeedback.setText(holder.tvFeedbackMessage.getText());

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the edited feedback message
                String editedMessage = editTextFeedback.getText().toString();

                // Call a method to update the feedback with the new message
                updateFeedbackMessage(feedback, editedMessage);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void updateFeedbackMessage(Feedback feedback, String editedMessage) {
        // Implement your logic to update the feedback message in the database
        // You may need to use the feedback object or its ID to perform the update
        // Once the update is successful, update the UI accordingly
        feedback.setMessage(editedMessage);
        notifyDataSetChanged(); // Notify the adapter about the data change
    }
}
