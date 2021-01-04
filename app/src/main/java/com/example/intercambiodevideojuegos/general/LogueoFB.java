package com.example.intercambiodevideojuegos.general;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.admin.MenuAdmin;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.usuario.MenuUsuario;
import com.firebase.ui.auth.AuthUI;;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LogueoFB extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(); //Obtengo la referencia a firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logueo_f_b);

        if(user!=null)
        {
            verificacion();
        }
        else
        {
            //Los tipos de servicio
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build()
            );

            //Se Se obtienen las sesiones
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),1
            );

            final Button verificacion = findViewById(R.id.BotonVerificado);
            verificacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verificacion();
                }
            });
        }
    }

    //La respuesta del logueo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference userDB = reference.child("ListaUsuarios").child(user.getUid()); //Aqui creare mi usuario
        userDB.addListenerForSingleValueEvent(new  ValueEventListener() { //El listener
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class); //lo obtengo
                if(usuario==null)
                {
                    usuario = new Usuario();
                    usuario.setNombre(user.getDisplayName());
                    usuario.setCorreo(user.getEmail());
                    usuario.setId(user.getUid());
                    userDB.setValue(usuario);
                }
                final TextView mensaje = findViewById(R.id.menasajeCorreo);
                final Button verificacion = findViewById(R.id.BotonVerificado);
                user.reload();
                if(user.isEmailVerified())
                {
                    mensaje.setVisibility(View.GONE);
                    verificacion.setVisibility(View.GONE);
                    dondeVoy(usuario);
                }
                else
                {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mensaje.setVisibility(View.VISIBLE);
                            verificacion.setVisibility(View.VISIBLE);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Se obtiene el usuario y se verifica si es uno valido
    public void verificacion()
    {
        user.reload();
        if (user.isEmailVerified())
        {
            DatabaseReference userDB = reference.child("ListaUsuarios").child(user.getUid());
            userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    dondeVoy(usuario);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Su email aun no se encuentra verificado",Toast.LENGTH_SHORT).show();
        }
    }

    public void dondeVoy(Usuario usuario)
    {
        Intent intent;
        if(usuario.isAdmin()) //Evaluo si es admin o no
            intent = new Intent(getApplicationContext(), MenuAdmin.class);
        else
            intent = new Intent(getApplicationContext(), MenuUsuario.class);
        startActivity(intent);
        finish();
    }

}