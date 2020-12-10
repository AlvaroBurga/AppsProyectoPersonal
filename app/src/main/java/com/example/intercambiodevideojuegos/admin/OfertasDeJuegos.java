package com.example.intercambiodevideojuegos.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.adapters.OfertaDeJuegoAdapter;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OfertasDeJuegos extends AppCompatActivity {

    Usuario sesion;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    ArrayList<Videojuego> videojuegos = new ArrayList<>();
    String TAG = "msg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas_de_juegos);
        final ArrayList<StorageReference> imgRefs = new ArrayList<>();
        DatabaseReference gameRef = reference.child("listaVideojuegos");
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Videojuego videojuego = snapshot.getValue(Videojuego.class);
                    if(videojuego.getEstado().equalsIgnoreCase("Pendiente"))
                    {
                        videojuegos.add(videojuego);
                        imgRefs.add(storage.child("listaVideojuegos").child(snapshot.getKey()));
                    }

                }
                RecyclerView rv = findViewById(R.id.rvOfertaDeJuego);
                OfertaDeJuegoAdapter adapter = new OfertaDeJuegoAdapter(videojuegos,OfertasDeJuegos.this,imgRefs);
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("msgError", databaseError.getMessage() + databaseError.getDetails());
            }
        });



    }
}