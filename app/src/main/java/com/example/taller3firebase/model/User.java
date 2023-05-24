package com.example.taller3firebase.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@IgnoreExtraProperties
public class User  implements Serializable {
    private String id;
    private String name;
    private String lastName;
    private double latitude;
    private double longitude;
    private String numID;
    private boolean available;
    public boolean isAvailable() {
        return available;
    }


}
