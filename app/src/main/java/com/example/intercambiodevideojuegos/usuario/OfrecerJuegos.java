package com.example.intercambiodevideojuegos.usuario;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.general.LogueoFB;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

//todo ver el savedInstanceState
public class OfrecerJuegos extends AppCompatActivity {

    Usuario sesion;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    Uri imgref = null;
    int numJuegos;
    byte[] imbytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofrecer_juegos);

        //Se obtiene la cantidad de videojuegos
        StorageReference listaVideojuegos = storage.child("listaVideojuegos");
        listaVideojuegos.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                numJuegos=listResult.getItems().size();
            }
        });

        //Se obitene la sesion
        DatabaseReference userDB = reference.child("ListaUsuarios").child(user.getUid());
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sesion = dataSnapshot.getValue(Usuario.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("msg", databaseError.getMessage() + databaseError.getDetails());
            }
        });

        //Se obtienen los campos
        final EditText titulo = findViewById(R.id.tituloOfrecer);
        final Spinner consola = findViewById(R.id.consolaOfrecer);
        final EditText direccion = findViewById(R.id.direccionOfrecer);
        final ImageView imagen = findViewById(R.id.imagenOfrecer);
        ImageButton agregarImagen = findViewById(R.id.agregarImagenOfrecer);
        ImageButton seleccionarImagen = findViewById(R.id.seleccionarImagenOfrecer);
        Button ofrecer = findViewById(R.id.ofrecerBoton);
        final EditText otro = findViewById(R.id.otraConsolaOfrecer);

        //Activar el Spinner del tipo de consola
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

        //Se gestiona cuando se selecciona un elemento
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
                int permiso = ContextCompat.checkSelfPermission(OfrecerJuegos.this, Manifest.permission.CAMERA);
                if (permiso== PackageManager.PERMISSION_GRANTED)
                {
                    tomarFoto();
                }
                else ActivityCompat.requestPermissions(OfrecerJuegos.this,new String[]{Manifest.permission.CAMERA},1);
            }
        });

        seleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*"); //Permite seleccionar solo imagenes
                startActivityForResult(intent, 2);
            }
        });

        //Se gestiona el post el firebase
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

                //Si t'odo es vailido se hace el post
                if(valido)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OfrecerJuegos.this);
                    builder.setTitle("Confirmar oferta");
                    builder.setMessage("Debe garantizar que el titulo sea correcto, la imagen muestre el disco o cartucho original" +
                            "y se tenga una dirección correcta. De lo contrario el administrador cancelara su oferta");

                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Videojuego videojuego = new Videojuego();

                            videojuego.setId(numJuegos);
                            videojuego.setEstado("Pendiente");
                            videojuego.setDueñoOriginal(sesion);
                            videojuego.setRecojo(direccion.getText().toString());
                            videojuego.setTitulo(titulo.getText().toString());
                            if (consola.getSelectedItem().toString().equalsIgnoreCase("Otro"))
                                videojuego.setConsola(otro.getText().toString());
                            else videojuego.setConsola(consola.getSelectedItem().toString());
                            //Añadir el videojuego a la lista (POST)
                            reference.child("listaVideojuegos").child(String.valueOf(numJuegos)).setValue(videojuego);


                            //Obtener la imagen
                            String name = String.valueOf(numJuegos);
                            StorageReference imagenASubir=storage.child("listaVideojuegos").child(name);
                            UploadTask uploadTask;
                            if(imbytes==null) uploadTask = imagenASubir.putFile(imgref);
                            else uploadTask = imagenASubir.putBytes(imbytes);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    Toast.makeText(OfrecerJuegos.this,"Videojuego subido exitosamente",Toast.LENGTH_SHORT).show();
                                }
                            });
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(OfrecerJuegos.this,"Ocurrio un error",Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent intent = new Intent(getApplicationContext(),MenuUsuario.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();
                }
            }
        });
    }
    public void tomarFoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, 3);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            tomarFoto();
        }
        else
        {
            Toast.makeText(this, "No se tienen permisos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==2)
        {
            try {
                imgref = data.getData();
                ImageView imagen = findViewById(R.id.imagenOfrecer);
                imagen.setImageURI(imgref);
                imagen.setVisibility(View.VISIBLE);
                imbytes=null;
            }catch (Exception e){
                Toast.makeText(this,"no se selecciono imagen",Toast.LENGTH_SHORT).show();
            }

        }

        if (requestCode==3)
        {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView imagen = findViewById(R.id.imagenOfrecer);
                imagen.setImageBitmap(imageBitmap);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                assert imageBitmap != null;
                imageBitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
                imbytes = bos.toByteArray();
                imgref=null;
                imagen.setVisibility(View.VISIBLE);
            }catch (Exception e)
            {
                Toast.makeText(this,"no se selecciono imagen",Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save in savedInstanceState.
        savedInstanceState.putByteArray("imbytes",imbytes);
        if (imgref != null) savedInstanceState.putString("uri",imgref.toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state from the savedInstanceState.

        if(savedInstanceState != null) {
            ImageView imagen = findViewById(R.id.imagenOfrecer);
            Log.d("prueba", "onRestoreInstanceState:"+ "llegue aqui");
            imbytes = savedInstanceState.getByteArray("imbytes");
            if (imbytes!= null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imbytes,0,imbytes.length);
                imagen.setImageBitmap(bitmap);
                imagen.setVisibility(View.VISIBLE);
            }
            String aux = savedInstanceState.getString("uri");
            if (aux != null)
            {
                imgref=Uri.parse(aux);
                imagen.setImageURI(imgref);
                imagen.setVisibility(View.VISIBLE);
            }
        }

    }

}