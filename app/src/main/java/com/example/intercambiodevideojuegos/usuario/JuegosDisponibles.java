package com.example.intercambiodevideojuegos.usuario;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.entities.VideojuegosAdapter;

import java.util.ArrayList;

public class JuegosDisponibles extends AppCompatActivity {
    Videojuego[] videojuegos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juegos_disponibles);

        //Obtener la lista de videojuegos disponibles (get)

        //Mostrarlos los videojuegos en el recycler view
        VideojuegosAdapter videojuegosAdapter = new VideojuegosAdapter(videojuegos,JuegosDisponibles.this,null,null);
        RecyclerView rv = findViewById(R.id.listaJuegosDisponiblesU);
        rv.setAdapter(videojuegosAdapter);
        rv.setLayoutManager(new LinearLayoutManager(JuegosDisponibles.this));

        //Filtro de titulo
        final EditText filtroTitulo = findViewById(R.id.filtroTitulo);
        ImageButton lupa = findViewById(R.id.buscarTituloDisponible);
        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filtro = filtroTitulo.getText().toString();
                VideojuegosAdapter videojuegosAdapter = new VideojuegosAdapter(videojuegos,JuegosDisponibles.this,filtro,"c");
                RecyclerView rv = findViewById(R.id.listaJuegosDisponiblesU);
                rv.setAdapter(videojuegosAdapter);
                rv.setLayoutManager(new LinearLayoutManager(JuegosDisponibles.this));
            }
        });

        //Filtro de consola
        ArrayList<String> consolas=new ArrayList<>(); //Se crea el arreglo de las posibles consolas
        consolas.add("Todas las consolas"); //Situación inicial
        for (Videojuego i: videojuegos) //Se recorre para var las consolas de todos los videojuegos
        {
            boolean repite=false;
            for (String j : consolas) //Con esto se evita la repeticion de consolas
            {
                if(i.getConsola().equals(j)) repite=true;
            }
            if (!repite) consolas.add(i.getConsola());  //Se asigna solo si es una nueva consola
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,consolas);
        Spinner spinner = findViewById(R.id.buscarConsola);
        spinner.setAdapter(adapter); //Se pone en la vista
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getSelectedItem();
                if(item != null) {
                    String consola = item.toString();
                    VideojuegosAdapter videojuegosAdapter = new VideojuegosAdapter(videojuegos,JuegosDisponibles.this,consola,"t");
                    RecyclerView rv = findViewById(R.id.listaJuegosDisponiblesU);
                    rv.setAdapter(videojuegosAdapter);
                    rv.setLayoutManager(new LinearLayoutManager(JuegosDisponibles.this));
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
}