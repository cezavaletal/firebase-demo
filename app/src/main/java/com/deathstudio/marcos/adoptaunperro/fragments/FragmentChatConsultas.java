package com.deathstudio.marcos.adoptaunperro.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.deathstudio.marcos.adoptaunperro.Chat_REC;
import com.deathstudio.marcos.adoptaunperro.R;
import com.deathstudio.marcos.adoptaunperro.pojo.ChatConsulta;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class FragmentChatConsultas extends Fragment implements AIListener{

    @BindView(R.id.fab_img) ImageView fab_img;
    @BindView(R.id.recyclerViewChat) RecyclerView recyclerView;
    @BindView(R.id.editText) EditText editText;
    @BindView(R.id.addBtn) RelativeLayout addBtn;
    Boolean flagFab = true;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private AIService aiService;
    Intent intent;
    Result result;
    FirebaseRecyclerAdapter<ChatConsulta,Chat_REC> adapter;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String idUsuario;
int permiso;
    public FragmentChatConsultas(){}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_chat_consultas, container, false);
        ButterKnife.bind(this,view);

        accederPermisos();

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        final AIConfiguration config = new AIConfiguration("35251e6e02f44abb9be0f19033d5e091",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(getContext(), config);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();

        firebaseAuth = FirebaseAuth.getInstance();
        idUsuario = firebaseAuth.getCurrentUser().getUid();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = editText.getText().toString().trim();

                if (!message.equals("")) {

                    ChatConsulta chatMessage = new ChatConsulta(message, firebaseAuth.getCurrentUser().getDisplayName());
                    databaseReference.child("usuarios").child(idUsuario).child("consultas").push().setValue(chatMessage);

                    aiRequest.setQuery(message);
                    new AsyncTask<AIRequest,Void,AIResponse>(){

                        @Override
                        protected AIResponse doInBackground(AIRequest... aiRequests) {
                            final AIRequest request = aiRequests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(AIResponse response) {
                            if (response != null) {

                                Result result = response.getResult();
                                String reply = result.getFulfillment().getSpeech();
                                ChatConsulta chatMessage = new ChatConsulta(reply, "bot");
                                databaseReference.child("usuarios").child(idUsuario).child("consultas").push().setValue(chatMessage);
                            }
                        }
                    }.execute(aiRequest);
                }
                else {
                    //aiService.startListening();
                    intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                            "Pregunta algo");
                    try {
                        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getContext(),
                                "Sorry! Your device doesn\\'t support speech input",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                editText.setText("");

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //ButterKnife.bind(this);
                Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);


                if (s.toString().trim().length()!=0 && flagFab){
                    ImageViewAnimatedChange(getContext(),fab_img,img);
                    flagFab=false;

                }
                else if (s.toString().trim().length()==0){
                    ImageViewAnimatedChange(getContext(),fab_img,img1);
                    flagFab=true;

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FirebaseRecyclerOptions<ChatConsulta> options =
                new FirebaseRecyclerOptions.Builder<ChatConsulta>()
                        .setQuery(databaseReference.child("usuarios").child(idUsuario).child("consultas"), ChatConsulta.class)
                        .build();

        adapter = new  FirebaseRecyclerAdapter<ChatConsulta,Chat_REC>(options) {

            @Override
            public Chat_REC onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.lista_mensaje, parent, false);

                return new Chat_REC(view);
            }

            @Override
            protected void onBindViewHolder(Chat_REC holder, int position,ChatConsulta model) {
                if (model.getNombre().equals(firebaseAuth.getCurrentUser().getDisplayName())) {
                    holder.rightText.setText(model.getMensaje());

                    holder.rightText.setVisibility(View.VISIBLE);
                    holder.leftText.setVisibility(View.GONE);
                }
                else {
                    holder.leftText.setText(model.getMensaje());

                    holder.rightText.setVisibility(View.GONE);
                    holder.leftText.setVisibility(View.VISIBLE);
                }
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);

                }

            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }else{
                    Toast.makeText(getContext(),"Permiso denegado , no podra enviar mensaje por voz",Toast.LENGTH_LONG).show();
                }break;
            default: super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    private void accederPermisos(){
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Do something for lollipop and above versions

                permiso = getContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                if (permiso != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
                }
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }




    @Override
    public void onResult(AIResponse response) {
        result = response.getResult();
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    /**
     * probandooooooooooooooooooooo  1
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery=result.get(0);
                    //txtSpeechInput.setText(userQuery);
                    ChatConsulta chatMessage0 = new ChatConsulta(userQuery, firebaseAuth.getCurrentUser().getDisplayName());
                    databaseReference.child("usuarios").child(idUsuario).child("consultas").push().setValue(chatMessage0);

                    RetrieveFeedTask task=new RetrieveFeedTask();
                    task.execute(userQuery);
                }
                break;
            }

        }
    }

    /**
     * probandooooooooooooooooooooo  2
     */

    // Create GetText Metod
    public String GetText(String query) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://api.api.ai/v1/query?v=20150910");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "Bearer "+"35251e6e02f44abb9be0f19033d5e091");
            conn.setRequestProperty("Content-Type", "application/json");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", queryArray);
//            jsonParam.put("name", "order a medium pizza");
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d("karma", "after conversion is " + jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("karma", "json is " + jsonParam);

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();



            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
//            if (object.has("fulfillment")) {
            fulfillment = object.getJSONObject("fulfillment");
//                if (fulfillment.has("speech")) {
            speech = fulfillment.optString("speech");
//                }
//            }


            Log.d("karma ", "response is " + text);
            return speech;

        } catch (Exception ex) {
            Log.d("karma", "exception at last " + ex);
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }

        return null;
    }


    /**
     * probandooooooooooooooooooooo  3
     */

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... voids) {
            String s = null;
            try {

                s = GetText(voids[0]);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("karma", "Exception occurred " + e);
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);




            ChatConsulta chatMessage = new ChatConsulta(s, "bot");
            databaseReference.child("usuarios").child(idUsuario).child("consultas").push().setValue(chatMessage);

            // outputText.setText(s);



        }
    }
}
