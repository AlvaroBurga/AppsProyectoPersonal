package com.example.intercambiodevideojuegos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.entities.Videojuego;

public class ListaVideojuegosAdapter extends RecyclerView.Adapter<ListaVideojuegosAdapter.ViewHolder> {

    Videojuego[] videojuegos;
    Context context;
    String filtro;
    String tipo;

    public ListaVideojuegosAdapter(Videojuego[] videojuegos, Context context, @Nullable String filtro, String tipo)
    {
        this.context=context;
        this.videojuegos=videojuegos;
        this.filtro=filtro;
        this.tipo=tipo;
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
        Videojuego videojuego = videojuegos[position];
        holder.videojuego=videojuego;
        holder.context=context;
        holder.filtro=filtro;
        holder.tipo=tipo;
    }

    @Override
    public int getItemCount() {
        return videojuegos.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        Videojuego videojuego;
        Context context;
        String filtro;
        String tipo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Se aplican los filtros necesarios
            if(tipo.equalsIgnoreCase("t"))
            {
                if(!filtro.contains(videojuego.getTitulo())) {
                    itemView.setVisibility(View.GONE);
                }
            }
            if(tipo.equalsIgnoreCase("c"))
            {
                if(!filtro.contains(videojuego.getConsola())) {
                    itemView.setVisibility(View.GONE);
                }
            }
            if(!(videojuego.getEstado().equalsIgnoreCase("aceptado"))||
                    (videojuego.getEstado().equalsIgnoreCase("intercambiado")))
            {
                itemView.setVisibility(View.GONE);
            }

            TextView titulo = itemView.findViewById(R.id.tituloVideojuegoAdmin);
            TextView consola = itemView.findViewById(R.id.videojuegoConsolaAdmin);
            ImageView imagen = itemView.findViewById(R.id.imagenVideojuegoAdmin);
            TextView dueño = itemView.findViewById(R.id.dueñoNombreApellido);

            titulo.setText(videojuego.getTitulo());
            consola.setText(videojuego.getConsola());
       //     imagen.setImageURI(videojuego.getFoto());
            dueño.setText(videojuego.getDueñoOriginal().getNombre());
        }
    }
}
