package com.example.taller3firebase.location;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LocationUtils {
    private static final String TAG = "LocationUtils";

    public static List<LocationData> parseLocationsFromJson(Context context) {
        List<LocationData> locations = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("locations.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonContent = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonContent);
            JSONArray locationsArray = jsonObject.getJSONArray("locations");

            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject locationObject = locationsArray.getJSONObject(i);
                double latitude = locationObject.getDouble("latitude");
                double longitude = locationObject.getDouble("longitude");
                String name = locationObject.getString("name");

                LocationData locationData = new LocationData(name, latitude, longitude);
                locations.add(locationData);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error parsing JSON file: " + e.getMessage());
        }
        return locations;
    }

}


