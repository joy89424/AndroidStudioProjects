package com.example.practice2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonViewHolder> {
    private List<Person> personList;

    public PersonAdapter(List<Person> personList) {
        this.personList = personList;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Person person = personList.get(position);
        holder.nameTextView.setText(person.getName());
        holder.ageTextView.setText(String.valueOf(person.getAge()));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }
}
