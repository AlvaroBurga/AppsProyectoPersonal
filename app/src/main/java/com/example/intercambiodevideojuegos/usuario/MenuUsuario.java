package com.example.intercambiodevideojuegos.usuario;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.general.LogueoFB;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuUsuario extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    String notificacionId = "aceptacion";
    Usuario sesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_usuario);

        DatabaseReference userDB = reference.child("ListaUsuarios").child(user.getUid());
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sesion = dataSnapshot.getValue(Usuario.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Obtener los elementos
        Button juegosDisponibles = findViewById(R.id.juegosDisponiblesMenuU);
        Button ofrecerJuegos = findViewById(R.id.ofrecerJuegosMenuU);
        Button juegosOfrecidos = findViewById(R.id.juegosOfrecidoMenuU);
        TextView bienvenida = findViewById(R.id.bienvenidaMenuUsuario);

        String mensajeBienvenido = "Bienvenido " + user.getDisplayName();
        bienvenida.setText(mensajeBienvenido);

        //Activar los Botones
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

        //Se crea el canal de notificacion cuando se acepta el request
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(
                    notificacionId,
                    "Aceptación de videojuegos",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Esta notificacion les informará si su videojuego es aceptado o no" );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        reference.child("listaVideojuegos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Videojuego videojuego = dataSnapshot.getValue(Videojuego.class);
                assert videojuego != null;
                if (videojuego.getDueñoOriginal().getId().equalsIgnoreCase(user.getUid()) &&
                        videojuego.getEstado().equalsIgnoreCase("aceptado"))
                {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MenuUsuario.this,notificacionId);
                    builder.setSmallIcon(R.drawable.check);
                    builder.setContentTitle("Su videojuego fue aceptado");
                    builder.setContentText("Se le informa que su videojuego con nombre " + videojuego.getTitulo() + " fue aceptado");
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationManager.notify(1, builder.build());
                }

                if (videojuego.getDueñoOriginal().getId().equalsIgnoreCase(user.getUid()) &&
                        videojuego.getEstado().equalsIgnoreCase("cancelado"))
                {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MenuUsuario.this,notificacionId);
                    builder.setSmallIcon(R.drawable.check);
                    builder.setContentTitle("Su videojuego fue cancelado");
                    builder.setContentText("Se le informa que su videojuego con nombre " + videojuego.getTitulo() + " fue cancelado " +
                            "por el siguiente motivo: " + videojuego.getRespuesta());
                    builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    notificationManager.notify(2, builder.build());
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
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