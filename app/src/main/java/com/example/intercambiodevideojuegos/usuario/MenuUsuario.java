package com.example.intercambiodevideojuegos.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.intercambiodevideojuegos.R;

public class MenuUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);

        Button juegosDisponibles = findViewById(R.id.juegosDisponiblesMenuU);
        Button ofrecerJuegos = findViewById(R.id.ofrecerJuegosMenuU);
        Button juegosOfrecidos = findViewById(R.id.juegosOfrecidoMenuU);

        juegosDisponibles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),JuegosDisponibles.class);
                startActivity(intent);
            }
        });

        ofrecerJuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),OfrecerJuegos.class);
                startActivity(intent);
            }
        });

        juegosOfrecidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),JuegosOfrecidos.class);
                startActivity(intent);
            }
        });

    }
}