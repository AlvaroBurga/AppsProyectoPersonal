package com.example.intercambiodevideojuegos.entities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intercambiodevideojuegos.R;
import com.example.intercambiodevideojuegos.usuario.ReservarVideojuego;

public class VideojuegosAdapter extends RecyclerView.Adapter<VideojuegosAdapter.ViewHolder> {

    private Videojuego[] videojuegos;
    private Context context;
    private String filtro;
    private String tipoFiltro;


    public VideojuegosAdapter(Videojuego[] videojuegos, Context context, @Nullable String filtro, @Nullable String tipo) {
        this.videojuegos = videojuegos;
        this.context = context;
        this.filtro=filtro;
        this.tipoFiltro =tipo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.videojuego,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Videojuego videojuego = videojuegos[position];
        holder.videojuego=videojuego;
        holder.context=context;
        holder.filtro=filtro;
        holder.tipoFiltro=tipoFiltro;
    }

    @Override
    public int getItemCount() {
        return videojuegos.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        Videojuego videojuego;
        String filtro;
        String tipoFiltro;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            //El filtrado
            if(tipoFiltro.equalsIgnoreCase("t"))
            {
                if(!filtro.contains(videojuego.getTitulo())) {
                    itemView.setVisibility(View.GONE);
                }
            }
            if(tipoFiltro.equalsIgnoreCase("c"))
            {
                if(!filtro.contains(videojuego.getConsola())) {
                    itemView.setVisibility(View.GONE);
                }
            }
            if(!videojuego.getEstado().equalsIgnoreCase("aceptado"))
            {
                itemView.setVisibility(View.GONE);
            }

            //Se llena el view holder
            TextView titulo = itemView.findViewById(R.id.tituloVideojuego);
            TextView consola = itemView.findViewById(R.id.videojuegoConsola);
            ImageView imagen = itemView.findViewById(R.id.imagenVideojuego);
            Button accion = itemView.findViewById(R.id.accionVideojuego);

            titulo.setText(videojuego.getTitulo());
            consola.setText(videojuego.getConsola());
            imagen.setImageURI(videojuego.getFoto());

            accion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), ReservarVideojuego.class);
                    intent.putExtra("juego",videojuego);
                    context.startActivity(intent);
                }
            });


        }
    }
}
