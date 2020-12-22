package com.example.intercambiodevideojuegos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Usuario;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.example.intercambiodevideojuegos.usuario.JuegosDisponibles;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class VideojuegosAdapter extends RecyclerView.Adapter<VideojuegosAdapter.ViewHolder> {

    private ArrayList<Videojuego> videojuegos;
    private ArrayList<StorageReference> imRefs;
    private Context context;
    private Usuario sesion;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    public VideojuegosAdapter(ArrayList<Videojuego> videojuegos, Context context, Usuario sesion,ArrayList<StorageReference> imRefs) {
        this.videojuegos = videojuegos;
        this.context = context;
        this.sesion=sesion;
        this.imRefs = imRefs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.videojuego,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Videojuego videojuego = videojuegos.get(position);
        holder.context=context;
        final JuegosDisponibles juegosDisponibles = (JuegosDisponibles) context;

        holder.titulo.setText(videojuego.getTitulo());
        holder.consola.setText(videojuego.getConsola());

        int pos = Integer.parseInt(String.valueOf(videojuego.getId()));
        StorageReference imagen = imRefs.get(pos);
        Glide.with(context).load(imagen).into(holder.imagen);

        holder.accion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if(sesion.getPuntos()>0)
                {
                    builder.setTitle("Confirmar Solicitud");
                    builder.setMessage("Se ha solicitado reservar el videojuego "+ videojuego.getTitulo() + " para la consola " + videojuego.getConsola());
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sesion.setPuntos(sesion.getPuntos()-1);
                            reference.child("ListaUsuarios").child(sesion.getId()).setValue(sesion);
                            Toast.makeText(context, "le quedan " + sesion.getPuntos() + " puntos", Toast.LENGTH_SHORT).show();

                            videojuego.setEstado("Intercambiado");
                            reference.child("listaVideojuegos").child(String.valueOf(videojuego.getId())).setValue(videojuego);
                            juegosDisponibles.obtenerVideojuegosDisponibles();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
                else {
                    builder.setTitle("Puntos insuficientes");
                    builder.setMessage("No tiene suficientes puntos para solicitar el videojuego " + videojuego.getTitulo() +
                            " para la consola " + videojuego.getConsola() + ". Se recomienda ofrecer un nuevo juego");
                    builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                }
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return videojuegos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView titulo ;
        TextView consola;
        ImageView imagen;
        Button accion ;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            //Se llena el view holder
             titulo = itemView.findViewById(R.id.tituloVideojuego);
             consola = itemView.findViewById(R.id.videojuegoConsola);
             imagen = itemView.findViewById(R.id.imagenVideojuego);
             accion = itemView.findViewById(R.id.accionVideojuego);
        }
    }
}
