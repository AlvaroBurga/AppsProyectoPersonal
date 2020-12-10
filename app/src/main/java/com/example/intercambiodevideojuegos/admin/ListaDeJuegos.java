package com.example.intercambiodevideojuegos.admin;

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
import com.example.intercambiodevideojuegos.adapters.ListaVideojuegosAdapter;
import com.example.intercambiodevideojuegos.entities.Videojuego;

import java.util.ArrayList;

public class ListaDeJuegos extends AppCompatActivity {

    Videojuego [] videojuegos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_juegos);

        //Obtener la lista de juegos

        //Mostrarlos los videojuegos en el recycler view
        ListaVideojuegosAdapter listaVideojuegosAdapter = new ListaVideojuegosAdapter(videojuegos, ListaDeJuegos.this,null,null);
        RecyclerView rv = findViewById(R.id.rvTodosLosJuegos);
        rv.setAdapter(listaVideojuegosAdapter);
        rv.setLayoutManager(new LinearLayoutManager(ListaDeJuegos.this));

        final EditText filtroTitulo = findViewById(R.id.filtroTitulo);
        ImageButton lupa = findViewById(R.id.buscarTituloDisponible);
        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filtro = filtroTitulo.getText().toString();
                ListaVideojuegosAdapter listaVideojuegosAdapter = new ListaVideojuegosAdapter(videojuegos, ListaDeJuegos.this,filtro,"t");
                RecyclerView rv = findViewById(R.id.rvTodosLosJuegos);
                rv.setAdapter(listaVideojuegosAdapter);
                rv.setLayoutManager(new LinearLayoutManager(ListaDeJuegos.this));
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
                    ListaVideojuegosAdapter listaVideojuegosAdapter = new ListaVideojuegosAdapter(videojuegos, ListaDeJuegos.this,consola,"c");
                    RecyclerView rv = findViewById(R.id.rvTodosLosJuegos);
                    rv.setAdapter(listaVideojuegosAdapter);
                    rv.setLayoutManager(new LinearLayoutManager(ListaDeJuegos.this));
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}