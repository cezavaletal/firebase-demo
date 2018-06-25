package com.deathstudio.marcos.adoptaunperro.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.DetalleActivity;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.pojo.Publicacion;
import com.deathstudio.marcos.adoptaunperro.pojo.Usuario;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorPublicacion extends RecyclerView.Adapter<AdaptadorPublicacion.PublicacionViewHolder>{

    private List<Publicacion> publicacionList;
    private List<Usuario> usuarioList;
    private Context context;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    public  AdaptadorPublicacion(Context context){
        this.context = context;
    }

    public AdaptadorPublicacion(List<Publicacion> publicacionList, Context context) {
        this.publicacionList = publicacionList;
        this.context = context;
    }
//https://www.youtube.com/watch?v=0DH2tZjJtm0
//https://firebase.google.com/docs/cloud-messaging/admin/send-messages?hl=es-419

    @Override
    public PublicacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publicacion_cardview,parent,false);
        context = parent.getContext();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PublicacionViewHolder holder, int position) {

        Publicacion publicacion = publicacionList.get(position);

        holder.descripcion.setText(publicacion.getDescripcion());

        String fotoPerro = publicacion.getFotoPublicacion();
        Glide.with(context)
                .load(fotoPerro)
                .into(holder.imagen);
        holder.fecha.setText(publicacion.getFecha());

        String idUsuario = publicacion.getUid();

        mDatabase.child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                holder.nombrePublicador.setText(usuario.getNombre());
                //holder.nombrePublicador.setText(usuario.getNombre());
                Glide.with(context.getApplicationContext())
                        .load(usuario.getFoto())
                        .into(holder.imagenPublicador);
                Log.d("anastacio",usuario.getFoto());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), DetalleActivity.class);
                i.putExtra("fotoPerro",fotoPerro);
                i.putExtra("idUsuario" ,idUsuario);
                view.getContext().startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (publicacionList.isEmpty()) {
            return 0;
        } else {
            return publicacionList.size();
        }
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder{

        private ImageView imagen;
        private CircleImageView imagenPublicador;
        private TextView descripcion,nombrePublicador,fecha;


        public PublicacionViewHolder(View itemView) {
            super(itemView);

            imagenPublicador =itemView.findViewById(R.id.fotoPublicador);
            descripcion = itemView.findViewById(R.id.detalle);
            nombrePublicador = itemView.findViewById(R.id.nombrePublicador);
            fecha = itemView.findViewById(R.id.fecha);
            imagen = itemView.findViewById(R.id.foto);
        }
    }
}
