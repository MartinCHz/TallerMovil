package com.example.taller3firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonIniciar;
    Button buttonRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonIniciar = findViewById(R.id.button);
        buttonRegistrar = findViewById(R.id.button2);

        buttonIniciar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, IniciarSesion.class);
            startActivity(intent);
        });

        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Registrarse.class);
                startActivity(intent);
            }
        });
    }
}