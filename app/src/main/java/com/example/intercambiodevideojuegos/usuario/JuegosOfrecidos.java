package com.example.intercambiodevideojuegos.usuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.HistorialVideojuegoAdapter;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.entities.VideojuegosAdapter;

public class JuegosOfrecidos extends AppCompatActivity {

    Videojuego[] videojuegos;
    Usuario sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juegos_ofrecidos);

        //Obtener la lista de los juegos del usuario en sesion en el FB

        HistorialVideojuegoAdapter historialVideojuegoAdaptervideojuegosAdapter = new HistorialVideojuegoAdapter(videojuegos,JuegosOfrecidos.this, sesion);
        RecyclerView rv = findViewById(R.id.rvHistorial);
        rv.setAdapter(historialVideojuegoAdaptervideojuegosAdapter);
        rv.setLayoutManager(new LinearLayoutManager(JuegosOfrecidos.this));

    }
}