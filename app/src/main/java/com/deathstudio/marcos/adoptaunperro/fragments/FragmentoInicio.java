package com.deathstudio.marcos.adoptaunperro.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.deathstudio.marcos.adoptaunperro.Configuracion.Configuracion;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.adapter.AdaptadorPublicacion;
import com.deathstudio.marcos.adoptaunperro.pojo.Publicacion;

import java.util.ArrayList;
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

    private RecyclerView recyclerView;
    private AdaptadorPublicacion adaptadorPublicacion;
    private List<Publicacion> publicacionList = new ArrayList<>();
    Intent iin;
    Bundle b;

    public FragmentoInicio() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmento_inicio, container, false);



        publicacionList = Configuracion.LIST_PUB;
        //Collections.reverse(publicacionList);
        adaptadorPublicacion = new AdaptadorPublicacion(publicacionList,view.getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        /*recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        recyclerView.setItemAnimator(new DefaultItemAnimator());*/

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adaptadorPublicacion);

        iin= getActivity().getIntent();
        b = iin.getExtras();

     /*   Uri foto = estaaa;
        String genero = b.getString(GENERO);
        int edad = b.getInt(EDAD);
        String telefono = b.getString(TELEFONO);
        String descripcion = b.getString(DESCRIPCION);

        adaptadorPublicacion.addPublicacion(foto,genero,edad,telefono,descripcion);
        */

        return view;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == adaptadorPublicacion.getItemCount()) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = getArguments();
                Uri foto = estaaa;
                String genero = bundle.getString(GENERO);
                int edad = bundle.getInt(EDAD);
                String telefono = bundle.getString(TELEFONO);
                String descripcion = bundle.getString(DESCRIPCION);

                adaptadorPublicacion.addPublicacion(foto, genero, edad, telefono, descripcion);
                Toast.makeText(getActivity(), String.valueOf("Lista : " + publicacionList.size()), Toast.LENGTH_LONG).show();
            }
        }
    }
}
