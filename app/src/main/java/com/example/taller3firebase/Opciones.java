package com.example.taller3firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.taller3firebase.model.User;
import com.example.taller3firebase.service.BasicJobIntentService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Opciones extends AppCompatActivity {

    Button buttonCerrar, buttonDisponibilidad, buttonMapa;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    TextView dispo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
        buttonCerrar = findViewById(R.id.button6);
        buttonDisponibilidad = findViewById(R.id.button5);
        buttonMapa = findViewById(R.id.buttonMap);
        dispo = findViewById(R.id.textView5);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        dispon(myRef);

        buttonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Opciones.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonDisponibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User p = new User();
                if(dispo.getText() == "Disponible"){
                    dispo.setText("No disponible");

                }else{
                    dispo.setText("Disponible");
                    p.setAvailable(true);
                    Intent intent = new Intent(Opciones.this, BasicJobIntentService.class);
                    BasicJobIntentService.enqueueWork(Opciones.this, intent); // Envia el trabajo a BasicJobIntentService

                }
            }
        });

        buttonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    public void dispon(DatabaseReference myRef){
        FirebaseUser user = mAuth.getCurrentUser();

        myRef.child("user").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User newUser = snapshot.getValue(User.class);
                if(newUser.isAvailable()){
                    dispo.setText("Disponible");
                }else{
                    dispo.setText("No disponible");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}