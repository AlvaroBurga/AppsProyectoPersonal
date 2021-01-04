package com.example.intercambiodevideojuegos.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.adapters.ListaVideojuegosAdapter;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.general.LogueoFB;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;




public class ListaDeJuegos extends AppCompatActivity {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    ArrayList<Videojuego> videojuegos = new ArrayList<>();
    String TAG = "msg";
    String filtroConsola=null;
    String filtroTitulo=null;
    String filtroEstado=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_juegos);

        //Obtener la lista de juegos
        final DatabaseReference gameRef = reference.child("listaVideojuegos");
        final ArrayList<StorageReference> imgRefs = new ArrayList<>();

        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Mostrarlos los videojuegos en el recycler view
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    videojuegos.add(snapshot.getValue(Videojuego.class));
                    imgRefs.add(storage.child("listaVideojuegos").child(snapshot.getKey()));
                }

                ListaVideojuegosAdapter listaVideojuegosAdapter = new ListaVideojuegosAdapter(videojuegos, ListaDeJuegos.this, imgRefs);
                RecyclerView rv = findViewById(R.id.rvTodosLosJuegos);
                rv.setAdapter(listaVideojuegosAdapter);
                rv.setLayoutManager(new LinearLayoutManager(ListaDeJuegos.this));


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
                        new ArrayAdapter<String>(ListaDeJuegos.this, android.R.layout.simple_spinner_dropdown_item,consolas);
                Spinner spinner = findViewById(R.id.consolaListaTodos);
                spinner.setAdapter(adapter); //Se pone en la vista
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object item = parent.getSelectedItem();
                        if(item != null) {
                            if (position==0) filtroConsola=null;
                            else filtroConsola = item.toString();
                            ListaVideojuegosAdapter listaVideojuegosAdapter = filtrado(imgRefs);
                            RecyclerView rv = findViewById(R.id.rvTodosLosJuegos);
                            rv.setAdapter(listaVideojuegosAdapter);
                            rv.setLayoutManager(new LinearLayoutManager(ListaDeJuegos.this));
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //Filtro de estado
                ArrayList<String> estados=new ArrayList<>(); //Se crea el arreglo de las posibles estados
                estados.add("Todos los estados"); //Situación inicial
                for (Videojuego i: videojuegos) //Se recorre para var las consolas de todos los videojuegos
                {
                    boolean repite=false;
                    for (String j : estados) //Con esto se evita la repeticion de estados
                    {
                        if(i.getEstado().equals(j)) repite=true;
                    }
                    if (!repite) estados.add(i.getEstado());  //Se asigna solo si es un nuevo estado
                }
                ArrayAdapter<String> adapter2 =
                        new ArrayAdapter<String>(ListaDeJuegos.this, android.R.layout.simple_spinner_dropdown_item,estados);
                Spinner spinner2 = findViewById(R.id.estadoListaTodos);
                spinner2.setAdapter(adapter2); //Se pone en la vista
                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object item = parent.getSelectedItem();
                        if(item != null) {
                            if (position==0) filtroEstado=null;
                            else  filtroEstado = item.toString();
                            ListaVideojuegosAdapter listaVideojuegosAdapter = filtrado(imgRefs);
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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

        final EditText filtroTituloView = findViewById(R.id.buscarTituloTodos);
        ImageButton lupa = findViewById(R.id.botonBuscarTitulo);
        lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroTitulo = filtroTituloView.getText().toString();
                ListaVideojuegosAdapter listaVideojuegosAdapter = filtrado(imgRefs);
                RecyclerView rv = findViewById(R.id.rvTodosLosJuegos);
                rv.setAdapter(listaVideojuegosAdapter);
                rv.setLayoutManager(new LinearLayoutManager(ListaDeJuegos.this));
            }
        });


    }

    public ListaVideojuegosAdapter filtrado(ArrayList<StorageReference> imgRefs)
    {
        ArrayList<Videojuego> listaFiltrada = (ArrayList<Videojuego>) videojuegos.clone();

        if (filtroConsola!= null)
        {
            for (Videojuego i : videojuegos) if (!i.getConsola().equalsIgnoreCase(filtroConsola)) listaFiltrada.remove(i);
        }
        if (filtroEstado!= null)
        {
            for (Videojuego i : videojuegos) if (!i.getEstado().equalsIgnoreCase(filtroEstado)) listaFiltrada.remove(i);
        }
        if (filtroTitulo!= null)
        {
            for (Videojuego i : videojuegos) if (!i.getTitulo().contains(filtroTitulo)) listaFiltrada.remove(i);
        }
        TextView noResultados = findViewById(R.id.ceroResults);
        if (listaFiltrada.size()==0) noResultados.setVisibility(View.VISIBLE);
        else noResultados.setVisibility(View.GONE);

        return new ListaVideojuegosAdapter(listaFiltrada, this, imgRefs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barra_menu_admin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(getApplicationContext(), LogueoFB.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}