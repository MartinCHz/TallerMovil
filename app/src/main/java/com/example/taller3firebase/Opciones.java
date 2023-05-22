package com.example.taller3firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Opciones extends AppCompatActivity {

    Button buttonCerrar, buttonDisponibilidad, buttonMapa, buttonListarUsuarios;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    TextView dispo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
        buttonListarUsuarios = findViewById(R.id.button4);
        buttonDisponibilidad = findViewById(R.id.button5);
        buttonMapa = findViewById(R.id.buttonMap);
        buttonCerrar = findViewById(R.id.button6);
        dispo = findViewById(R.id.textView5);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        buttonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Opciones.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        buttonDisponibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dispo.getText() == "Disponible"){
                    dispo.setText("No disponible");
                }else{
                    dispo.setText("Disponible");
                }
            }
        });
        buttonListarUsuarios.setOnClickListener(view -> {
            Intent intent = new Intent(Opciones.this, ListarUsuarios.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
    }
}