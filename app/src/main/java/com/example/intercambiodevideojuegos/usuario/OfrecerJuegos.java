package com.example.intercambiodevideojuegos.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;

import java.util.ArrayList;

public class OfrecerJuegos extends AppCompatActivity {

    Usuario sesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofrecer_juegos);

        final EditText titulo = findViewById(R.id.tituloOfrecer);
        final Spinner consola = findViewById(R.id.consolaOfrecer);
        final EditText direccion = findViewById(R.id.direccionOfrecer);
        final ImageView imagen = findViewById(R.id.imagenOfrecer);
        ImageButton agregarImagen = findViewById(R.id.agregarImagenOfrecer);
        Button ofrecer = findViewById(R.id.ofrecerBoton);
        final EditText otro = findViewById(R.id.otraConsolaOfrecer);

        //Activar el Spinner
        ArrayList<String> consolas=new ArrayList<>(); //Se crea el arreglo de las posibles consolas
        consolas.add("Seleccionar consola");
        consolas.add("Nintendo Switch");
        consolas.add("Play station 4");
        consolas.add("X-box One");
        consolas.add("PC");
        consolas.add("Otro");
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,consolas);
        consola.setAdapter(adapter);

        consola.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().equalsIgnoreCase("Otro"))
                {
                    otro.setVisibility(View.VISIBLE);
                }
                else
                {
                    otro.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Funcionamiento de obtencion de la imagen
        agregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Funcionamiento para obtener la imagen y agregarla al IV
                imagen.setVisibility(View.VISIBLE);
            }
        });

        ofrecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean valido = true;
                if (titulo.getText().toString().equalsIgnoreCase(""))
                {
                    titulo.setError("El videojuego debe tener un titulo");
                    valido = false;
                }
                if (direccion.getText().toString().equalsIgnoreCase(""))
                {
                    direccion.setError("Se debe indicar una direccion en donde se rocogera el juego");
                    valido = false;
                }
                if (otro.getText().toString().equalsIgnoreCase("") &&
                        consola.getSelectedItem().toString().equalsIgnoreCase("Otro"))
                {
                    otro.setError("Se debe indicar la consola");
                    valido = false;
                }
                if (consola.getSelectedItemPosition()==0)
                {
                    ((TextView) consola.getSelectedView()).setError("Se debe seleccionar una consola");
                    valido = false;
                }

                //Valdiar que la imagen no sea nula
                if(imagen.getVisibility()==View.GONE)
                {
                    TextView error = findViewById(R.id.cidj);
                    error.setError("Se debe agregar una imagen");
                    valido=false;
                }

                if(valido)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Confirmar oferta");
                    builder.setMessage("Debe garantizar que el titulo sea correcto, la imagen muestre el disco o cartucho original" +
                            "y se tenga una dirección correcta. De lo contrario el administrador cancelara su oferta");
                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Videojuego videojuego = new Videojuego();
                            videojuego.setEstado("Pendiente");
                            videojuego.setDueñoOriginal(sesion);
                            //Obtener la imagen
                            //videojuego.setFoto(imagen.getDrawable());
                            videojuego.setRecojo(direccion.getText().toString());
                            videojuego.setTitulo(titulo.getText().toString());
                            if (consola.getSelectedItem().toString().equalsIgnoreCase("Otro"))
                                videojuego.setConsola(otro.getText().toString());
                            else videojuego.setConsola(consola.getSelectedItem().toString());

                            //Añadir el videojuego a la lista (POST)

                            Intent intent = new Intent(getApplicationContext(),MenuUsuario.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
            }
        });
    }
}