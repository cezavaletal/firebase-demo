package com.deathstudio.marcos.adoptaunperro.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.Configuracion.Configuracion;
import com.deathstudio.marcos.adoptaunperro.MainActivity;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.pojo.Publicacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FragmentoPublicarAnuncio extends Fragment {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    static SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm");
    private static final int PICK_IMAGE = 100;
    public static Uri estaaa;  // jajaja buena varialejadsjjbsa
    private FirebaseAuth firebaseAuth;
    Uri imageUri;

    ImageView imagen;
    Spinner genero;
    EditText edad,telefono,descripcion;
    Button publcar,sua;

    String[] opciones = {"masculino","femenino"};



    public FragmentoPublicarAnuncio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmento_publicar_anuncio, container, false);

        publcar = (Button) view.findViewById(R.id.agregarPublicar);
        sua = (Button) view.findViewById(R.id.verificarImagenPublicar);
        imagen = (ImageView)view.findViewById(R.id.fotoPublicar);
        genero = (Spinner)view.findViewById(R.id.generoPublicar);
        edad = (EditText)view.findViewById(R.id.edadPublicar);
        telefono = (EditText)view.findViewById(R.id.telefonoPublicar);
        descripcion = (EditText)view.findViewById(R.id.descripcionPublicar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item,opciones);
        genero.setAdapter(adapter);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        publcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Configuracion.OPCION_MENU = true;
                Configuracion.SELECT_NAV = R.id.nav_home;
                Publicacion obj = new Publicacion();

                obj.setGenero(String.valueOf(genero.getSelectedItem()));
                obj.setDescripcion( String.valueOf(descripcion.getText()));
                obj.setEdad(Integer.parseInt(edad.getText().toString()));
                obj.setTelefono( String.valueOf(telefono.getText()));

                obj.setImagen(estaaa);

                Configuracion.LIST_PUB.add(obj);



                Calendar calendar = Calendar.getInstance();

                calendar.get(Calendar.HOUR);
                calendar.get(Calendar.MINUTE);
                firebaseAuth = FirebaseAuth.getInstance();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
                DatabaseReference currentUserDB = databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("publicaciones").child("fecha");

                currentUserDB.child("fecha").setValue(simpleDateFormat.format(calendar.getTime()));
                currentUserDB.child("hora").setValue(simpleDateFormatHora.format(new Date()));
                //currentUserDB.child("fotoPublicacion").setValue(estaaa.toString());
                currentUserDB.child("genero").setValue(genero.getSelectedItem().toString());
                currentUserDB.child("edad").setValue(edad.getText().toString());
                currentUserDB.child("telefono").setValue(telefono.getText().toString());
                currentUserDB.child("descripcion").setValue(descripcion.getText().toString());

                Intent intent = new Intent(getContext(),MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //agregar();
            }
        });

        return view;
    }






    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            //imagen.setImageURI(imageUri);

            Glide.with(getContext())
                    .load(imageUri)
                    .into(imagen);
            estaaa = imageUri;
        }
    }
}
