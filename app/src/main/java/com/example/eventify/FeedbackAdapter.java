package com.example.eventify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    private List<Feedback> feedbackList;

    // Constructor to receive the data
    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
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
            holder.tvFeedbackTitle.setText("feedback.getTitle()");
            holder.tvFeedbackDescription.setText("feedback.getDescription()");
            // Set other views as needed
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    // ViewHolder class to hold the views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFeedbackTitle;
        TextView tvFeedbackDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeedbackTitle = itemView.findViewById(R.id.tvFeedbackTitle);
            tvFeedbackDescription = itemView.findViewById(R.id.tvFeedbackDescription);
            // Initialize other views if needed
        }
    }
}
