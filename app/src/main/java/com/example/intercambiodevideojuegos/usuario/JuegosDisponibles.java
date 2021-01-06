package com.example.intercambiodevideojuegos.usuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.adapters.ListaVideojuegosAdapter;
import com.example.intercambiodevideojuegos.admin.ListaDeJuegos;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.adapters.VideojuegosAdapter;
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

//todo copiar el filtrado de lista de juegos
//Todo, solo mostrar los aceptados
public class JuegosDisponibles extends AppCompatActivity {
    Usuario sesion;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    String filtroConsola=null;
    String filtroTitulo=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juegos_disponibles);

        //primero se obtiene la sesion y luego se listan los juegos
        DatabaseReference userDB = reference.child("ListaUsuarios").child(user.getUid());
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sesion = dataSnapshot.getValue(Usuario.class);
                //De esta forma se obtienen los videojuegos
                obtenerVideojuegosDisponibles();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(JuegosDisponibles.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    public void obtenerVideojuegosDisponibles() {

        final ArrayList<Videojuego> videojuegos=new ArrayList<>(); //Aqui se almacenaran los videojuegos aceptados
        final DatabaseReference gameRef = reference.child("listaVideojuegos"); //Se obtiene la referencia a todos los videojuegos
        final ArrayList<StorageReference> imgRefs = new ArrayList<>(); //Lista donde se almacenaran las imagenes respectivas de los videojuegos
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Videojuego videojuego = ds.getValue(Videojuego.class);
                    if(videojuego.getEstado().equalsIgnoreCase("aceptado") &&
                            (!videojuego.getDueñoOriginal().getId().equalsIgnoreCase(user.getUid()))) videojuegos.add(videojuego); //Solo se muestran los aceptados
                    imgRefs.add(storage.child("listaVideojuegos").child(ds.getKey()));
                }

                //Mostrarlos los videojuegos en el recycler view
                VideojuegosAdapter videojuegosAdapter = new VideojuegosAdapter(videojuegos,JuegosDisponibles.this, sesion,imgRefs);
                listarVideojuegos(videojuegosAdapter);

                //El filtrado de consola
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
                        new ArrayAdapter<String>(JuegosDisponibles.this, android.R.layout.simple_spinner_dropdown_item,consolas);
                Spinner spinner = findViewById(R.id.buscarConsola);
                spinner.setAdapter(adapter); //Se pone en la vista
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Object item = parent.getSelectedItem();
                        if(item != null) {
                            if (position==0) filtroConsola=null;
                            else filtroConsola = item.toString();
                            listarVideojuegos(filtrado(imgRefs, videojuegos));
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                //Filtro de titulo
                final EditText filtroTituloET = findViewById(R.id.filtroTitulo);
                ImageButton lupa = findViewById(R.id.buscarTituloDisponible);
                lupa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filtroTitulo = filtroTituloET.getText().toString();
                        listarVideojuegos(filtrado(imgRefs, videojuegos));
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(JuegosDisponibles.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void listarVideojuegos(VideojuegosAdapter videojuegosAdapter)
    {
        RecyclerView rv = findViewById(R.id.listaJuegosDisponiblesU);
        rv.setAdapter(videojuegosAdapter);
        rv.setLayoutManager(new LinearLayoutManager(JuegosDisponibles.this));
    }

    public VideojuegosAdapter filtrado(ArrayList<StorageReference> imgRefs, ArrayList<Videojuego> videojuegos) {
        ArrayList<Videojuego> listaFiltrada = (ArrayList<Videojuego>) videojuegos.clone();

        if (filtroConsola!= null)
        {
            for (Videojuego i : videojuegos) if (!i.getConsola().equalsIgnoreCase(filtroConsola)) listaFiltrada.remove(i);
        }
        if (filtroTitulo!= null)
        {
            for (Videojuego i : videojuegos) if (!i.getTitulo().contains(filtroTitulo)) listaFiltrada.remove(i);
        }
        TextView noResultados = findViewById(R.id.noResults);
        if (listaFiltrada.size()==0) noResultados.setVisibility(View.VISIBLE);
        else noResultados.setVisibility(View.GONE);
        return new VideojuegosAdapter(listaFiltrada, this,sesion, imgRefs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barra_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), LogueoFB.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.id.mostrarPuntos:
                Toast.makeText(getApplicationContext(),String.valueOf(sesion.getPuntos()),Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}