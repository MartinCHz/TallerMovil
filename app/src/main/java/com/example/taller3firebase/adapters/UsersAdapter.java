package com.example.taller3firebase.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.taller3firebase.R;
import com.example.taller3firebase.databinding.UserAdapterLayoutBinding;
import com.example.taller3firebase.model.User;

import java.util.ArrayList;

public class UsersAdapter extends ArrayAdapter<User> {

    public UsersAdapter(Context context, ArrayList<User> people) {
        super(context, 0, people);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Grab the person to render
        User person = getItem(position);
        // Check if amn existing view is being reused, otherwise inflate the view
        if (convertView == null) {
//            convertView = binding.getRoot();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_adapter_layout, parent, false);
        }
        // Get all the fields from the adapter
        TextView firstName = convertView.findViewById(R.id.peopleFirstName);
        TextView lastName = convertView.findViewById(R.id.peopleLastName);
        TextView address = convertView.findViewById(R.id.peopleAddress);
        // Format and set the values in the view
        firstName.setText(person.getName());
        lastName.setText(person.getLastName());
        address.setText(String.format("%S , %S", person.getLatitude(), person.getLongitude()));
        return convertView;
    }
}