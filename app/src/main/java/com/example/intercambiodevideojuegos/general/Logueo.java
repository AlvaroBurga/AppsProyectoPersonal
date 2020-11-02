package com.example.intercambiodevideojuegos.general;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.admin.MenuAdmin;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.usuario.MenuUsuario;

public class Logueo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logueo);

        final EditText correoView = findViewById(R.id.correoLogin);
        final EditText contraseñaView = findViewById(R.id.contraseñaLogin);
        Button iniciar = findViewById(R.id.ingresarLogin);
        TextView registro = findViewById(R.id.registroLogin);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registrio.class);
                startActivityForResult(intent,1);
            }
        });

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valido = true;
                if(correoView.getText().toString().equals(""))
                {
                    correoView.setError("Debe ingresar el correo");
                    valido =false;
                }
                if(contraseñaView.getText().toString().equals(""))
                {
                    contraseñaView.setError("Debe ingresar el correo");
                    valido =false;
                }

                if(valido)
                {
                    Usuario usuario=validarIngreso(correoView.getText().toString(), contraseñaView.getText().toString());
                    if (usuario==null)
                    {
                        contraseñaView.setError("Correo o contraseña equivocados");
                    }
                    else
                    {
                        Intent intent;
                        if (usuario.isAdmin())
                        {
                            intent = new Intent(getApplicationContext(), MenuAdmin.class);
                            //iniciar la sesion
                            intent.putExtra("sesion",usuario);
                            startActivity(intent);
                            //Destruir login
                        }
                        else
                        {
                            intent = new Intent(getApplicationContext(), MenuUsuario.class);
                            //iniciar la sesion
                            intent.putExtra("sesion",usuario);
                            startActivity(intent);
                            //Destruir login
                        }
                    }
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode== RESULT_OK)
            Toast.makeText(getApplicationContext(),"Usuario creado",Toast.LENGTH_LONG).show();

        if (requestCode==1 && resultCode== RESULT_CANCELED)
            Toast.makeText(getApplicationContext(),"No se creo el usuario",Toast.LENGTH_SHORT).show();

    }

    public Usuario validarIngreso(String correo, String contraseña)
    {
        //Ver en la base de datos si existe el usuario con el correo dado
        //Validar La contraseña
        //Obtener el usuario y devolverlo
        Usuario usuario=null;
        if(correo.equals("a@admin.com")&& contraseña.equals("123"))
        {
            usuario = new Usuario();
            usuario.setAdmin(true);
            usuario.setCorreo(correo);
            usuario.setNombre("Camad");
            usuario.setApellido("Minas");
        }
        if(correo.equals("a@user.com")&& contraseña.equals("123"))
        {
            usuario = new Usuario();
            usuario.setCorreo(correo);
            usuario.setNombre("Manus");
            usuario.setApellido("Ernesto");
        }


        return usuario;
    }
}
