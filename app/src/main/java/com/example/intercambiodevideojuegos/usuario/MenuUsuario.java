package com.example.intercambiodevideojuegos.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Usuario;

public class MenuUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);

        Intent intent = getIntent();
        Usuario usuario = (Usuario) intent.getSerializableExtra("sesion");

        Button juegosDisponibles = findViewById(R.id.juegosDisponiblesMenuU);
        Button ofrecerJuegos = findViewById(R.id.ofrecerJuegosMenuU);
        Button juegosOfrecidos = findViewById(R.id.juegosOfrecidoMenuU);
        TextView bienvenida = findViewById(R.id.bienvenidaMenuUsuario);
        bienvenida.setText( "Bienvenido "+ usuario.getNombre() + " " + usuario.getApellido());

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