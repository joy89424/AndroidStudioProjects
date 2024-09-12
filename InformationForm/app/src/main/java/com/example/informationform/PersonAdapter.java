package com.example.informationform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private List<Person> personList;

    public PersonAdapter(List<Person> personList) {
        this.personList = personList;
    }

    @NonNull
    @Override
    public PersonAdapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.PersonViewHolder holder, int position) {
        Person person = personList.get(position);
        holder.nameTextView.setText(person.getName());
        holder.ageTextView.setText(String.valueOf(person.getAge()));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ageTextView;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
        }
    }
}
