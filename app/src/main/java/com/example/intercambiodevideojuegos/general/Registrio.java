package com.example.intercambiodevideojuegos.general;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Usuario;

public class Registrio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrio);

        final EditText nombre = findViewById(R.id.nombreRegistro);
        final EditText apellido =findViewById(R.id.apellidoRegistro);
        final EditText dni = findViewById(R.id.dniRegistro);
        final EditText correo = findViewById(R.id.correoRegistro);
        final EditText contraseña = findViewById(R.id.contraseñaRegistro);
        final EditText confirmacion = findViewById(R.id.cofirmacionContraseñaRegistro);
        Button submit = findViewById(R.id.submitRegistro);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valido=true;
                if (nombre.getText().toString().equals(""))
                {
                    nombre.setError("Se debe tener un nombre");
                    valido = false;
                }
                if (apellido.getText().toString().equals(""))
                {
                    apellido.setError("Se debe tener un apellido");
                    valido = false;
                }
                if (dni.getText().toString().equals(""))
                {
                    dni.setError("Se debe tener un dni");
                    valido = false;
                }
                else
                {
                    if(dni.getText().toString().length()!=8)
                    {
                        dni.setError("El dni debe tener 8 caracteres");
                        valido = false;
                    }
                }

                if (contraseña.getText().toString().equals(""))
                {
                    contraseña.setError("Se debe tener una contraseña");
                    valido = false;
                }
                if (!confirmacion.getText().toString().equals(contraseña.getText().toString()))
                {
                    confirmacion.setError("La contraseña no coincide");
                    valido = false;
                }
                if (correo.getText().toString().equals(""))
                {
                    correo.setError("Se debe incluir un correo");
                    valido = false;
                }

                //Validar que el correo no se repita

                if (valido)
                {
                    Usuario usuario = new Usuario();
                    usuario.setApellido(apellido.getText().toString());
                    usuario.setNombre(nombre.getText().toString());
                    usuario.setDni(dni.getText().toString());
                    usuario.setCorreo(correo.getText().toString());
                    usuario.setContraseña(contraseña.getText().toString());
                    //Post al firebase
                    setResult(RESULT_OK);
                    finish();

                }

            }
        });

    }
}