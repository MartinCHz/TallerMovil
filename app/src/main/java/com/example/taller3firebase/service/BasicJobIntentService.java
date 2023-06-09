package com.example.taller3firebase.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taller3firebase.MapsActivity;
import com.example.taller3firebase.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.taller3firebase.R;
import com.example.taller3firebase.model.DatabasePaths;

public class BasicJobIntentService extends JobIntentService {
    // Id for the service
    private static final int JOB_ID = 12;

    public static final String CHANNEL_ID = "Taller03";
    private int notificationId;

    // Variables for Firebase DB
    FirebaseDatabase database;
    DatabaseReference myRef;
    ValueEventListener valueEventListener;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    // Aux method to queue the task.
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, BasicJobIntentService.class, JOB_ID, intent);
    }

    // Method that executes in background.
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        notificationId = 1;
        //Initialize Firebase database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        currentUser = mAuth.getCurrentUser();
        createNotificationChannel();
        loadSubscriptionUsers();

    }

    public void loadSubscriptionUsers() {
        myRef = database.getReference(DatabasePaths.USER);
        valueEventListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User newUser = singleSnapshot.getValue(User.class);
                    assert newUser != null;
                    if (newUser.isAvailable()) {
                        //if (newUser.isAvailable() && currentUser != null && currentUser.getUid().equals(singleSnapshot.getKey())) {
                        //if (newUser.isAvailable() && !currentUser.getUid().equals(singleSnapshot.getKey())) {
                        // Simple Notification example
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);
                        mBuilder.setSmallIcon(R.drawable.sinfoto);
                        mBuilder.setContentTitle("Se conecto un usuario");
                        mBuilder.setContentText("El usuario " + newUser.getName() + " " + newUser.getLastName() + " esta conectado");
                        //Acción asociada a la notificación
                        Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                        String id = singleSnapshot.getKey();
                        intent.putExtra("key", id);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), Integer.parseInt(newUser.getNumID()), intent, PendingIntent.FLAG_IMMUTABLE);
                        mBuilder.setContentIntent(pendingIntent);
                        mBuilder.setAutoCancel(true); //Remueve la notificación cuando se toca

                        //Lanzar la notificacion
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());
                        if (ActivityCompat.checkSelfPermission(BasicJobIntentService.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        notificationManager.notify(Integer.parseInt(newUser.getNumID()), mBuilder.build());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Background", "error en la consulta por subscripcions", databaseError.toException());
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //IMPORTANCE_MAX MUESTRA LA NOTIFICACIÓN ANIMADA
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
