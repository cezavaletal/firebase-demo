package com.deathstudio.marcos.adoptaunperro.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.pojo.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDetallePublicador extends Fragment {

    @BindView(R.id.detalleFotoPublicador) CircleImageView detalleFotoPublicador;
    @BindView(R.id.detalleNombrePublicador) TextView detalleNombrePublicador;
    private DatabaseReference mDatabase;
    private Context context;

    public FragmentDetallePublicador(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //http://www.materialpalette.com/yellow/teal  #5E5E5E
        //https://github.com/pedant/sweet-alert-dialog
        View view = inflater.inflate(R.layout.fragment_detalle_publicador, container, false);
        ButterKnife.bind(this,view);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("usuarios");
        context = container.getContext();

        Bundle bundle = getArguments();

        String id = bundle.getString("idUsuario");

        mDatabase.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                detalleNombrePublicador.setText(usuario.getNombre());
                //holder.nombrePublicador.setText(usuario.getNombre());
                Glide.with(context.getApplicationContext())
                        .load(usuario.getFoto())
                        .into(detalleFotoPublicador);
                Log.d("anastacio",usuario.getFoto());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return view;
    }

}
