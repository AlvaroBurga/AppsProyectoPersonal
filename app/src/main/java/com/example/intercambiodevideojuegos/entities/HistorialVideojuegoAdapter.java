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

public class HistorialVideojuegoAdapter  extends RecyclerView.Adapter<HistorialVideojuegoAdapter.ViewHolder> {
    private Videojuego[] videojuegos;
    private Context context;
    private Usuario sesion;


    public HistorialVideojuegoAdapter(Videojuego[] videojuegos, Context context, Usuario sesion) {
        this.videojuegos = videojuegos;
        this.context = context;
        this.sesion=sesion;
    }

    @NonNull
    @Override
    public HistorialVideojuegoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.videojuego_historia,parent,false);
        HistorialVideojuegoAdapter.ViewHolder viewHolder = new HistorialVideojuegoAdapter.ViewHolder(itemView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Videojuego videojuego = videojuegos[position];
        holder.videojuego=videojuego;
        holder.context=context;
        holder.sesion = sesion;
    }


    @Override
    public int getItemCount() {
        return videojuegos.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        Videojuego videojuego;
        Usuario sesion;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            //Se llena el view holder
            TextView titulo = itemView.findViewById(R.id.tituloVideojuegoHistorial);
            TextView consola = itemView.findViewById(R.id.videojuegoHistorialConsola);
            TextView estado = itemView.findViewById(R.id.estadoVideojuegoHistorial);
            ImageView imagen = itemView.findViewById(R.id.imagenVideojuegoHistorial);
            Button accion = itemView.findViewById(R.id.accionVideojuegoHistorial);

            titulo.setText(videojuego.getTitulo());
            consola.setText(videojuego.getConsola());
            imagen.setImageURI(videojuego.getFoto());

            if (videojuego.getEstado().equalsIgnoreCase("borrado"))
            {
                itemView.setVisibility(View.GONE);
            }

            if (videojuego.getEstado().equalsIgnoreCase("pendiente")||
                    videojuego.getEstado().equalsIgnoreCase("aceptado"))
            {
                accion.setText("Recuperar");
                accion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        if(sesion.getPuntos()>0)
                        {
                            builder.setTitle("Confirmar retiro");
                            builder.setMessage("Seguro que desea retirar el videojuego "+ videojuego.getTitulo() + " para la consola " + videojuego.getConsola());
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sesion.setPuntos(sesion.getPuntos()-1);
                                    videojuego.setEstado("Borrado");
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
                            builder.setMessage("No tiene suficientes puntos para recuperar el videojuego " + videojuego.getTitulo() +
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

            if (videojuego.getEstado().equalsIgnoreCase("intercambiado"))
            {
                accion.setText("Borrar");
                accion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Borrar registro");
                        builder.setMessage("Seguro que desea borrar el registro del juego");
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                videojuego.setEstado("Borrado");
                                //Refrescar la pantalla
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                });
            }

            if (videojuego.getEstado().equalsIgnoreCase("cancelado"))
            {
                accion.setText("Detalles");
                accion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Su videojuego no fue aceptado por el siguiente motivo");
                        builder.setMessage(videojuego.getRespuesta());
                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                });
            }
        }
    }
}
