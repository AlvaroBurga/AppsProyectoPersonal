package com.example.intercambiodevideojuegos.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.adapters.OfertaDeJuegoAdapter;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.general.LogueoFB;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class OfertasDeJuegos extends AppCompatActivity {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    ArrayList<Videojuego> videojuegos = new ArrayList<>();
    String TAG = "msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas_de_juegos);
        final ArrayList<StorageReference> imgRefs = new ArrayList<>();
        final DatabaseReference gameRef = reference.child("listaVideojuegos");
        final RecyclerView rv = findViewById(R.id.rvOfertaDeJuego);



        //Se obtiene la lista
        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Videojuego videojuego = snapshot.getValue(Videojuego.class);
                    imgRefs.add(storage.child("listaVideojuegos").child(snapshot.getKey()));
                    //Solo se mostraran los estados pendientes
                    if(videojuego.getEstado().equalsIgnoreCase("Pendiente"))
                    {
                        videojuegos.add(videojuego);
                    }

                }

                //Se pone en el adaptar
                OfertaDeJuegoAdapter adapter = new OfertaDeJuegoAdapter(videojuegos,OfertasDeJuegos.this,imgRefs);

                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                if(videojuegos.size()==0) ceroResultados();

                //Para actualizar la lista en caso se agrega o se modifique
                gameRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        //TODO gestionar cuando se agrega un elemento (dificil)
                        /*
                        Videojuego videojuego = dataSnapshot.getValue(Videojuego.class);
                        videojuegos.add(videojuego);
                        OfertaDeJuegoAdapter ofertaDeJuegoAdapter = (OfertaDeJuegoAdapter) rv.getAdapter();
                        ofertaDeJuegoAdapter.notifyDataSetChanged(); //Esto gestiona en caso se agregue un elemento a tiempo real
                         */

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (rv.getAdapter()!=null)
                        {
                            Videojuego videojuego = dataSnapshot.getValue(Videojuego.class);
                            if (videojuego.getEstado()!="Pendiente")
                            {
                                //Se detecta la posicion del objeto a eliminar
                                int id = Integer.parseInt(String.valueOf(videojuego.getId()));
                                int posision=obtenerPos(id,videojuegos);

                                //Se procede a modificar el rv
                                if(posision>=0)
                                {
                                    videojuegos.remove(posision);
                                    OfertaDeJuegoAdapter ofertaDeJuegoAdapter = (OfertaDeJuegoAdapter) rv.getAdapter();
                                    rv.removeViewAt(posision);
                                    ofertaDeJuegoAdapter.notifyItemRemoved( posision);
                                    ofertaDeJuegoAdapter.notifyItemRangeChanged(posision,videojuegos.size());
                                    if (videojuegos.size()==0) ceroResultados();
                                }

                            }
                            if(videojuego.getEstado().equalsIgnoreCase("aceptado"))
                            {
                                String idUser = videojuego.getDueñoOriginal().getId();
                                reference.child("ListaUsuarios").child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                        usuario.setPuntos(usuario.getPuntos()+1);
                                        reference.child("ListaUsuarios").child(usuario.getId()).setValue(usuario);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, "onCancelled: " + databaseError.getDetails()+databaseError.getMessage());
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: " + databaseError.getDetails() + databaseError.getMessage());
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("msgError", databaseError.getMessage() + databaseError.getDetails());
            }
        });


    }

    public int obtenerPos(int id, ArrayList<Videojuego> lista)
    {
        int posision=-1;
        int cont=0;
        for (Videojuego i : lista) {
            if (id == i.getId()) {
                posision = cont;
            }
            cont++;
        }
        return posision;
    }

    public void ceroResultados()
    {
        TextView tv = findViewById(R.id.ceroOfertas);
        tv.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barra_menu_admin,menu);
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
        }
        return super.onOptionsItemSelected(item);
    }


}