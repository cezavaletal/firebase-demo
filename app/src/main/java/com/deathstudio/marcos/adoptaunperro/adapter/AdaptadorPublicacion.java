package com.deathstudio.marcos.adoptaunperro.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.pojo.Publicacion;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorPublicacion extends RecyclerView.Adapter<AdaptadorPublicacion.PublicacionViewHolder>{

    private List<Publicacion> publicacionList;
    private Context context;

    public  AdaptadorPublicacion(Context context){
        this.context = context;
    }

    public AdaptadorPublicacion(List<Publicacion> publicacionList, Context context) {
        this.publicacionList = publicacionList;
        this.context = context;
    }

    @Override
    public PublicacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.publicacion_cardview,parent,false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PublicacionViewHolder holder, int position) {

        String facebookUserId="";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            for (UserInfo profile : user.getProviderData()) {
                // verifica si el id coincide en fb
                if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    facebookUserId = profile.getUid();
                }
            }

            String photoUrll = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";

            ImageView foto1 = holder.imagenPublicador;
            Glide.with(context)
                    .load(photoUrll)
                    .into(foto1);

            TextView nombrePublicador = holder.nombrePublicador;
            nombrePublicador.setText(name);

            Uri imagen = publicacionList.get(position).getImagen();
            String descripcion = publicacionList.get(position).getDescripcion();

            ImageView foto = holder.imagen;

            Glide.with(context)
                    .load(imagen)
                    .into(foto);

            TextView desc = holder.descripcion;
            desc.setText(descripcion);
        }

    }

    @Override
    public int getItemCount() {
        if (publicacionList.isEmpty()) {
            return 0;
        } else {
            return publicacionList.size();
        }
    }

    public void addPublicacion(Uri imagen, String genero, int edad, String telefono, String descripcion){
        Publicacion publicacion  = new Publicacion();
        publicacion.setImagen(imagen);
        publicacion.setGenero(genero);
        publicacion.setEdad(edad);
        publicacion.setTelefono(telefono);
        publicacion.setDescripcion(descripcion);

        publicacionList.add(publicacion);

        notifyDataSetChanged();
        //notifyItemInserted(getItemCount());
    }

    public class PublicacionViewHolder extends RecyclerView.ViewHolder{

        private ImageView imagen;
        private CircleImageView imagenPublicador;
        private TextView descripcion,nombrePublicador;


        public PublicacionViewHolder(View itemView) {
            super(itemView);

            imagenPublicador = (CircleImageView)itemView.findViewById(R.id.fotoPublicador);
            imagen = (ImageView)itemView.findViewById(R.id.foto);
            descripcion = (TextView)itemView.findViewById(R.id.detalle);
            nombrePublicador = (TextView)itemView.findViewById(R.id.nombrePublicador);
        }
    }
}
