package com.example.informationform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private List<Person> personList;
    private OnDeleteClickListenr onDeleteClickListenr;

    public interface OnDeleteClickListenr {
        void onDeleteClick(int postion);
    }

    public PersonAdapter(List<Person> personList, OnDeleteClickListenr listener) {
        this.personList = personList;
        this.onDeleteClickListenr = listener;
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
        holder.btnDelete.setOnClickListener(view -> onDeleteClickListenr.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return personList == null ? 0 : personList.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, ageTextView;
        Button btnDelete;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textName);
            ageTextView = itemView.findViewById(R.id.textAge);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
