package com.example.intercambiodevideojuegos.entities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class VideojuegosAdapter extends RecyclerView.Adapter<VideojuegosAdapter.ViewHolder> {

    private Videojuego[] videojuegos;
    private Context context;
    private String filtro;
    private String tipoFiltro;
    private Usuario sesion;


    public VideojuegosAdapter(Videojuego[] videojuegos, Context context, Usuario sesion,@Nullable String filtro, @Nullable String tipo) {
        this.videojuegos = videojuegos;
        this.context = context;
        this.filtro=filtro;
        this.tipoFiltro =tipo;
        this.sesion=sesion;
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
        holder.sesion = sesion;
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
        Usuario sesion;

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    if(sesion.getPuntos()>0)
                    {
                        builder.setTitle("Confirmar Solicitud");
                        builder.setMessage("Se ha solicitado reservar el videojuego "+ videojuego.getTitulo() + " para la consola " + videojuego.getConsola());
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sesion.setPuntos(sesion.getPuntos()-1);
                                videojuego.setEstado("Intercambiado");
                                //Refrescar la pantalla
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
                        builder.setMessage("No tiene suficientes puntos para solicitaer el videojuego " + videojuego.getTitulo() +
                                " para la consola " + videojuego.getConsola() + ". Se recomienda ofrecer un nuevo juego");
                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                }
            });


        }
    }
}
