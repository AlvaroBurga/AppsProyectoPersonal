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
import android.widget.TextView;
import android.widget.Toast;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.adapters.HistorialVideojuegoAdapter;
import com.example.intercambiodevideojuegos.entities.Usuario;
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
import java.util.Objects;

public class JuegosOfrecidos extends AppCompatActivity {

    Usuario sesion;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juegos_ofrecidos);

        //Obtener la sesion del usuario
        DatabaseReference userDB = reference.child("ListaUsuarios").child(user.getUid());
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sesion = dataSnapshot.getValue(Usuario.class);
                listarHistorial();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(JuegosOfrecidos.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void listarHistorial()
    {
        final ArrayList<Videojuego> videojuegos = new ArrayList<>();
        final ArrayList<StorageReference> imgRefs = new ArrayList<>();
        //primero obtiene la lista de videojuegos
        reference.child("listaVideojuegos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Videojuego videojuego = ds.getValue(Videojuego.class);

                    //Se lista si el videojuego no esta borrado y si el dueño coincide
                    imgRefs.add(storage.child("listaVideojuegos").child(Objects.requireNonNull(ds.getKey())));
                    assert videojuego != null;
                    if( (!videojuego.getEstado().equalsIgnoreCase("borrado")) &&
                            videojuego.getDueñoOriginal().getId().equalsIgnoreCase(sesion.getId()))
                    {
                        videojuegos.add(videojuego);

                    }
                }

                //En caso no se tengan videojuegos, se mustra el mensaje
                TextView noResults = findViewById(R.id.ceroJuegosOfrecidos);
                if (videojuegos.size()==0) noResults.setVisibility(View.VISIBLE);
                else  noResults.setVisibility(View.INVISIBLE);

                //una vez teniendo la lista, se procede a listar con el recycler view
                HistorialVideojuegoAdapter historialVideojuegoAdaptervideojuegosAdapter = new HistorialVideojuegoAdapter(videojuegos,JuegosOfrecidos.this, sesion, imgRefs);
                RecyclerView rv = findViewById(R.id.rvHistorial);
                rv.setAdapter(historialVideojuegoAdaptervideojuegosAdapter);
                rv.setLayoutManager(new LinearLayoutManager(JuegosOfrecidos.this));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}