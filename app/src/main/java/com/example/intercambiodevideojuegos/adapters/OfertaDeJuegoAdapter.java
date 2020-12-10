package com.example.intercambiodevideojuegos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OfertaDeJuegoAdapter extends RecyclerView.Adapter<OfertaDeJuegoAdapter.ViewHolder>{

    ArrayList<Videojuego> videojuegos;
    Context context;
    ArrayList<StorageReference> imgRefs;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public OfertaDeJuegoAdapter(ArrayList<Videojuego> videojuegos, Context context, ArrayList<StorageReference> imgRefs)
    {
        this.context=context;
        this.videojuegos=videojuegos;
        this.imgRefs=imgRefs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(context).inflate(R.layout.oferta_videojuego,parent,false);
        ViewHolder viewHolder = new ViewHolder(item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Videojuego videojuego=videojuegos.get(position);
        holder.videojuego = videojuego;
        holder.context=context;
        holder.reference=reference.child("listaVideojuegos");

        int pos = Integer.parseInt(String.valueOf(videojuego.getId()));
        holder.titulo.setText(videojuego.getTitulo());
        holder.consola.setText(videojuego.getConsola());
        StorageReference imagen = imgRefs.get(pos);
        Glide.with(context).load(imagen).into(holder.imagen);
        holder.direccion.setText(videojuego.getRecojo());
        holder.dueño.setText(videojuego.getDueñoOriginal().getNombre());
    }

    @Override
    public int getItemCount() {
        return videojuegos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {

        Videojuego videojuego;
        Context context;
        DatabaseReference reference;

        TextView titulo;
        TextView consola;
        ImageView imagen;
        TextView direccion;
        TextView dueño;
        Button aceptar;
        Button cancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

             titulo = itemView.findViewById(R.id.tituloVideojuegoOferta);
             consola = itemView.findViewById(R.id.videojuegoOfertaConsola);
             imagen = itemView.findViewById(R.id.imagenVideojuegoOferta);
             direccion = itemView.findViewById(R.id.direccionOferta);
             dueño = itemView.findViewById(R.id.dueñoOferta);
             aceptar = itemView.findViewById(R.id.aceptarVideojuegoOferta);
             cancelar = itemView.findViewById(R.id.cancelarVideojuegoOferta);

            aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videojuego.setEstado("aceptado");
                    reference.child(String.valueOf(videojuego.getId())).setValue(videojuego);
                    //Refrescar pantalla
                }
            });

            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Justificacion de negacion");
                    builder.setMessage("Ingrese la justificacion de la cancelacion");
                    final EditText justificacion = new EditText(context);
                    builder.setView(justificacion);
                    builder.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (justificacion.getText().toString().equals(""))
                            {
                                justificacion.setError("Se debe indicar una justificacion");
                            }
                            else
                            {
                                videojuego.setRespuesta(justificacion.getText().toString());
                                videojuego.setEstado("cancelado");
                                reference.child(String.valueOf(videojuego.getId())).setValue(videojuego);
                                //Refrescar pantasha
                            }
                        }
                    });
                    builder.show();
                }
            });

        }
    }
}
