package com.example.eventify;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView card_txt;


    public MyViewHolder(@NonNull View itemView, MyAdapter.OnItemClickListener listener) {
        super(itemView);
        imageView = itemView.findViewById(R.id.card_icon);
        card_txt = itemView.findViewById(R.id.cart_title);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }
}