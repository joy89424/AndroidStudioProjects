package com.example.practice2;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class PersonViewHolder extends RecyclerView.ViewHolder {
    public TextView nameTextView;
    public TextView ageTextView;

    public PersonViewHolder(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.item_name);
        ageTextView = itemView.findViewById(R.id.item_age);
    }
}
