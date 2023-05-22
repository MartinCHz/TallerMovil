package com.example.taller3firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class User {

    private String name;
    private String lastName;
    private double latitude;
    private double longitude;
//    private String credentialId;
    private String numId;
    private boolean available;

    public boolean isAvailable() {
        return available;
    }

}
