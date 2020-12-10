package com.example.intercambiodevideojuegos.usuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.adapters.HistorialVideojuegoAdapter;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JuegosOfrecidos extends AppCompatActivity {

    Videojuego[] videojuegos;
    Usuario sesion;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

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