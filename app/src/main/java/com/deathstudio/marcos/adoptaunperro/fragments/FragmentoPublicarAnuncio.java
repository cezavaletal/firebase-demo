package com.deathstudio.marcos.adoptaunperro.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.Configuracion.Configuracion;
import com.deathstudio.marcos.adoptaunperro.MainActivity;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.pojo.Publicacion;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;

import com.google.api.services.vision.v1.model.AnnotateImageRequest;

import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;



import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import ai.api.GsonFactory;


public class FragmentoPublicarAnuncio extends Fragment {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    static SimpleDateFormat simpleDateFormatHora = new SimpleDateFormat("HH:mm");
    private static final int PICK_IMAGE = 1;
    public static String imagenrecibida;
    private FirebaseAuth firebaseAuth;
    StorageReference firebaseStorage;
    StorageReference storagePath;
    String idUsuario;
    Uri imageUri;
    ProgressDialog dialog;

    Spinner genero;
    EditText edad,telefono,descripcion;
    Button publcar;

    String[] opciones = {"Macho","Hembra"};

    //////////////////////////////////////////////////////////////////////////////

    private static final String CLOUD_API_KEY = "AIzaSyBm9YtC_OUDF8CiCeuk16mB3i3GDoLjHRs";

    ImageView imagen;
    android.speech.tts.TextToSpeech tts;
    ArrayList<String> listString;

    private Activity mActivity;
    //////////////////////////////////////////////////////////////////////////////



    public FragmentoPublicarAnuncio(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmento_publicar_anuncio, container, false);

        publcar =  view.findViewById(R.id.agregarPublicar);
        imagen = view.findViewById(R.id.fotoPublicar);
        genero = view.findViewById(R.id.generoPublicar);
        edad = view.findViewById(R.id.edadPublicar);
        telefono = view.findViewById(R.id.telefonoPublicar);
        descripcion = view.findViewById(R.id.descripcionPublicar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item,opciones);
        genero.setAdapter(adapter);

        publcar.setEnabled(false);



        listString = new ArrayList<String>();


        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listString.clear();
                //tts.speak("Lets GO", TextToSpeech.QUEUE_FLUSH,null);
                openGallery();
            }
        });



        ////////////////////////////////////////////////////////////

        firebaseStorage = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        idUsuario = firebaseAuth.getCurrentUser().getUid();

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

                obj.setFotoPublicacion(imagenrecibida);

                Configuracion.LIST_PUB.add(obj);

                //nombre de imagen
                String [] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(Uri.parse(imagenrecibida), filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                String selectedImages = filePath.substring(filePath.lastIndexOf("/")+1);
////////////////////////////////////////////////////////////////////////////////////////////////////////
                dialog = new ProgressDialog(getContext());

                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // progress.setIndeterminate(true);
                dialog.setMax(100);
                dialog.setMessage("Creando Publicación");

                dialog.setCancelable(false);
                dialog.show();
////////////////////////////////
                storagePath = firebaseStorage.child("publicacion_imagenes").child(selectedImages);
////////////////////////////////
                storagePath.putFile(Uri.parse(imagenrecibida)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                        if(task.isSuccessful()){

                            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Calendar calendar = Calendar.getInstance();

                                    calendar.get(Calendar.HOUR);
                                    calendar.get(Calendar.MINUTE);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("publicaciones").push();
                                    //DatabaseReference currentUserDB = databaseReference.child(idUsuario).child("publicaciones").push();
                                    Log.d("tag",databaseReference.child(idUsuario).child("publicaciones").push().getKey());

                                    databaseReference.child("fecha").setValue(simpleDateFormat.format(calendar.getTime()));
                                    databaseReference.child("hora").setValue(simpleDateFormatHora.format(new Date()));
                                    databaseReference.child("fotoPublicacion").setValue(uri.toString());
                                    databaseReference.child("genero").setValue(genero.getSelectedItem().toString());
                                    databaseReference.child("edad").setValue(Integer.parseInt(edad.getText().toString()));
                                    databaseReference.child("telefono").setValue(telefono.getText().toString());
                                    databaseReference.child("descripcion").setValue(descripcion.getText().toString());
                                    databaseReference.child("uid").setValue(idUsuario);

                                    Intent intent = new Intent(getContext(),MainActivity.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic("pushNotifications");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });



                        }else {
                            dialog.dismiss();
                        }

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        int currentprogress = (int) progress;

                        dialog.setProgress(currentprogress);

                    }
                });

            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE && data != null){
            imageUri = data.getData();

            Glide.with(getContext())
                    .load(imageUri)
                    .into(imagen);

            imagenrecibida = String.valueOf(imageUri);

            verificarImagen(imageUri);
        }
    }

    //private ProgressDialog dialog2;

    public void callCloudVision(final Bitmap bitmap) throws IOException{

        new AsyncTask<Object,String,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mActivity = getActivity();
                dialog = new ProgressDialog(getContext());
                dialog.setMessage("Verificando Imagen");
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected String doInBackground(Object... objects) {

             try {
                 HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();

                 JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

                 Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);

                 builder.setVisionRequestInitializer(new VisionRequestInitializer(CLOUD_API_KEY));

                 Vision vision = builder.build();

                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                 bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

                 byte[] imageBytes = byteArrayOutputStream.toByteArray();

                 AnnotateImageRequest request = new AnnotateImageRequest()
                                 .setImage(new Image().encodeContent(imageBytes))
                                 .setFeatures(ImmutableList.of(new Feature()
                                         .setType("LABEL_DETECTION")
                                         .setMaxResults(3)));

                 Vision.Images.Annotate annotate = vision.images().annotate(
                         new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
                 // Due to a bug: requests to Vision API containing large images fail when GZipped.
                 annotate.setDisableGZipContent(true);

                 BatchAnnotateImagesResponse batchResponse = annotate.execute();
                 assert batchResponse.getResponses().size() == 1;


                 /*for (int i = 0; i < listString.size(); i++) {
                     SystemClock.sleep(1500);
                     Log.d("listaaaaaaaaaaaa",listString.get(i));
                     publishProgress(listString.get(i));
                 }
*/
                 return converResponseToString(batchResponse);

             } catch (GoogleJsonResponseException e){
                 Log.d("ERROR", "CLOUD GoogleJsonResponseException : " + e.getMessage());
             } catch (IOException e) {
                 Log.d("ERROR", "CLOUD IOException : " + e.getMessage());
             }
                return "Error con Cloud Vision, inténtelo de nuevo >:v";
            }


            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                //Toast.makeText(mActivity,values[0],Toast.LENGTH_LONG).show();
                /*Toast.makeText(mActivity,"mmmmmmmmmmmmmmmmmmmmmmmm",Toast.LENGTH_LONG).show();
                Log.d("vegeta", String.valueOf(values[0]));
                System.err.println("MOSTRANDO");
                System.err.println("---------");
                System.err.println(values[0]);
                //tts.speak(values[0],TextToSpeech.QUEUE_ADD,null);
                kg+=(values[0]+"\n");

                Toast.makeText(getContext(),kg,Toast.LENGTH_LONG).show();*/
            }
            @Override
            protected void onPostExecute(String resultado) {
                super.onPostExecute(resultado);
                //Toast.makeText(mActivity,s,Toast.LENGTH_LONG).show();
                if (resultado.equals("Es un perro")){
                    Toast.makeText(mActivity,resultado,Toast.LENGTH_LONG).show();
                    publcar.setEnabled(true);
                    publcar.setBackgroundColor(Color.parseColor("#9b121b"));
                    publcar.setTextColor(Color.WHITE);
                }else{
                    Toast.makeText(mActivity,resultado,Toast.LENGTH_LONG).show();
                    imagen.setImageResource(R.drawable.subirfoto);
                }
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        }.execute();
    }

    public void verificarImagen(Uri uri){
        if(uri != null){
            try {
                //Toast.makeText(getContext(), "KHEEE", Toast.LENGTH_LONG).show();
                Bitmap bitmap = scaleBitmap(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),uri),1200);
                callCloudVision(bitmap);
            }catch (IOException e){
                Toast.makeText(getContext(),"Error",Toast.LENGTH_LONG).show();
                Log.d("nooooooooooooo","Error");
            }
        }else{
            Toast.makeText(getContext(),"Error imagen null",Toast.LENGTH_LONG).show();
            Log.d("nooooooooooooo","Error2");
        }
    }

    public Bitmap scaleBitmap(Bitmap bitmap,int dimens){
        int originalwidth = bitmap.getWidth();
        int originalheight = bitmap.getHeight();

        int resizewidth = dimens;
        int resizeheight = dimens;

        if (originalheight > originalwidth){
            resizeheight = dimens;
            resizewidth = (int) (resizeheight * (float) originalwidth / (float) originalheight);
        }else  if (originalwidth > originalheight){
            resizewidth = dimens;
            resizeheight = (int) (originalwidth * (float) resizeheight  / (float) originalwidth);
        }else  if (originalheight == originalwidth){
            resizeheight = dimens;
            resizewidth = dimens;
        }
        return Bitmap.createScaledBitmap(bitmap,resizewidth,resizeheight,false);

    }

    private String converResponseToString(BatchAnnotateImagesResponse response){

        String mensaje = "";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();

        List<String> list = new ArrayList<String>();

        if(labels != null){
            Log.d("suaaaaaaaaaa3", String.valueOf(response.getResponses().get(0).getLabelAnnotations()));
            for (EntityAnnotation label : labels){
                list.add(label.getDescription());
            }


            mensaje = (list.contains("dog") || list.contains("dogs"))? "Es un perro":"No es un perro >:v";
            Log.d("gatoconbotas", String.valueOf(list));
        }else{
            mensaje +="NADAAA";
        }

        return mensaje;

    }
}
