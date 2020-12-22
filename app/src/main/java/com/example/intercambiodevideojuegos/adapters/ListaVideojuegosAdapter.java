package com.example.intercambiodevideojuegos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Videojuego;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListaVideojuegosAdapter extends RecyclerView.Adapter<ListaVideojuegosAdapter.ViewHolder> {

    ArrayList<Videojuego> videojuegos;
    Context context;
    ArrayList<StorageReference> imgRefs;

    public ListaVideojuegosAdapter(ArrayList<Videojuego> videojuegos, Context context, ArrayList<StorageReference> imgRefs)
    {
        this.context=context;
        this.videojuegos=videojuegos;
        this.imgRefs = imgRefs;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.elemento_videojuego_cuando_lista_todo,parent,false);
        ListaVideojuegosAdapter.ViewHolder viewHolder = new ListaVideojuegosAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Videojuego videojuego = videojuegos.get(position);
        holder.context=context;
        holder.titulo.setText(videojuego.getTitulo());
        holder.consola.setText(videojuego.getConsola());
        holder.dueño.setText(videojuego.getDueñoOriginal().getNombre());
        holder.estado.setText(videojuego.getEstado());

        int pos = Integer.parseInt(String.valueOf(videojuego.getId()));
        StorageReference imagen = imgRefs.get(pos);
        Glide.with(context).load(imagen).into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return videojuegos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        Context context;
        TextView titulo;
        TextView consola;
        ImageView imagen;
        TextView dueño;
        TextView estado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             titulo = itemView.findViewById(R.id.tituloVideojuegoAdmin);
             consola = itemView.findViewById(R.id.videojuegoConsolaAdmin);
             imagen = itemView.findViewById(R.id.imagenVideojuegoAdmin);
             dueño = itemView.findViewById(R.id.dueñoNombreApellido);
             estado = itemView.findViewById(R.id.ponerEstadoCuandoListaTodo);

        }
    }
}
