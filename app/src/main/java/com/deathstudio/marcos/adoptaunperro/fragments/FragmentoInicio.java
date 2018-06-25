package com.deathstudio.marcos.adoptaunperro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.deathstudio.marcos.adoptaunperro.Configuracion.Configuracion;
import com.deathstudio.marcos.adoptaunperro.Error;
import com.deathstudio.marcos.adoptaunperro.MainActivity;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.adapter.AdaptadorPublicacion;
import com.deathstudio.marcos.adoptaunperro.pojo.ChatConsulta;
import com.deathstudio.marcos.adoptaunperro.pojo.Publicacion;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.deathstudio.marcos.adoptaunperro.fragments.FragmentoPublicarAnuncio.estaaa;


public class FragmentoInicio extends Fragment {

    private static final String DEBUG_TAG = "Fragment";
    public static final String FOTO = "foto";
    public static final String GENERO = "genero";
    public static final String EDAD = "edad";
    public static final String TELEFONO = "telefono";
    public static final String DESCRIPCION = "descripcion";
    public static final String TRANSITION_FAB = "fab_transition";

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private AdaptadorPublicacion adaptadorPublicacion;
    private List<Publicacion> publicacionList ;

    FirebaseRecyclerAdapter<Publicacion,AdaptadorPublicacion.PublicacionViewHolder> mAdapter;

    private DatabaseReference mDatabase;
    String idUsuario;
    Context context;
    public FragmentoInicio() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragmento_inicio, container, false);
        //final SwipeRefreshLayout swipeView = view.findViewById(R.id.swipe_container);
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        context = container.getContext();
        publicacionList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();

        adaptadorPublicacion = new AdaptadorPublicacion(publicacionList,getContext());

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setAdapter(adaptadorPublicacion);

        idUsuario = firebaseAuth.getCurrentUser().getUid();
//        DatabaseReference currentUserDB = mDatabase.child("publicaciones");
        //Query query = currentUserDB.child("genero");//.equalTo("masculino");  //currentUserDB.orderByChild("genero").equalTo("masculino");

        if (firebaseAuth.getCurrentUser() != null){
            mDatabase = FirebaseDatabase.getInstance().getReference("publicaciones");

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    publicacionList.clear();
                    for (DataSnapshot i: dataSnapshot.getChildren()){
                        Publicacion publicacion = i.getValue(Publicacion.class);
                        publicacionList.add(publicacion);
                    }
                    adaptadorPublicacion.notifyDataSetChanged();
                    /*adaptadorPublicacion = new AdaptadorPublicacion(publicacionList,getContext());
                    recyclerView.setAdapter(adaptadorPublicacion);*/

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        /*if (existeConexionInternet()) {





        } else {
            //Intent i = new Intent(getContext(),Error.class);
            //startActivity(i);
            //finish();
            Toast.makeText(getContext(),"No hay internet",Toast.LENGTH_LONG).show();
            view = inflater.inflate(R.layout.activity_error, container, false);

        }*/

/*        swipeView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_dark,
                android.R.color.holo_red_light);






        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                new android.os.Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        if (existeConexionInternet()) {

                        } else {
                            //view = inflater.inflate(R.layout.activity_error, container, false);

                        }
                    }
                }, 3000);

            }
        });*/

        return view;
    }

    public boolean existeConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    @Override
    public void onStart() {
        super.onStart();
        //mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //mAdapter.stopListening();
    }
}
