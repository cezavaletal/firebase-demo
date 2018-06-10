package com.deathstudio.marcos.adoptaunperro;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.deathstudio.marcos.adoptaunperro.pojo.Usuario;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.AuthProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{

    @BindView(R.id.khe) GifImageView progressBar2;
    @BindView(R.id.facebookView) Button botonIniciarFB;
    @BindView(R.id.loginButton) LoginButton loginButton;
    @BindView(R.id.googleView) Button botonGoogle;
    @BindView(R.id.googleButton) SignInButton signInButton;
    private GoogleSignInClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final int RC_SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        callbackManager = CallbackManager.Factory.create();


        inicializar();
        loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                //Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                Log.d("cagandola",error.getMessage());
            }
        });
        botonIniciarFB.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        botonGoogle.setOnClickListener(this);


    }

    public void inicializar(){
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Ya está logueado
                    irPantallaPrincipal();
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleApiClient = GoogleSignIn.getClient(this, gso);
    }

    /*****************************************************************************************
     *  Si está logueado ya no lo mandamos al login >:v
     ****************************************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount result1 = result.getSignInAccount();
                String s = result.getSignInAccount().getEmail();
                Log.d("verificandooooooo",s);
                FirebaseDatabase.getInstance().getReference().child("usuarios")
                        //.orderByChild("proveedor")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                List<String> usuarios = new ArrayList<>();
                                List<String> proo = new ArrayList<>();
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    Usuario usuario = childSnapshot.getValue(Usuario.class);
                                    if (usuario.getCorreo().equals(s) ){
                                    usuarios.add(usuario.getCorreo());
                                    proo.add(usuario.getProveedor());}
                                }
                                Log.d("verificandooooooo", String.valueOf(usuarios));
                                Log.d("verificandooooooo", String.valueOf(proo));
                                String proveedor = "google.com";

                                if (usuarios.contains(s) && !proo.contains(proveedor)){
                                    Toast.makeText(getApplicationContext(),"El correo que está usando ya esta en uso , por favor use otro", Toast.LENGTH_LONG).show();
                                    Log.d("verificandooooooo", "El correo que está usando ya esta en uso");
                                }else{
                                    Log.d("verificandooooooo", "Se puede registrar");
                                    handleGoogle(result1);
                                }

                                /*if (s.equals(childSnapshot.child("correo").getValue(String.class))) {
                                    Toast.makeText(getApplicationContext(),"El correo que está usando ya esta en uso , por favor use otro", Toast.LENGTH_LONG).show();
                                    Log.d("verificandooooooo", "Correo es igual al de " + childSnapshot.child("nombre").getValue(String.class));
                                }else{
                                    //handleGoogle(result1);
                                    Log.d("verificandooooooo", "Se puede registrar");
                                }*/

                                // usuarios ahora contiene los nombres de todos los usuarios.
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                //handleGoogle(result1);
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*****************************************************************************************
     *  Si el usuario inició sesión se usa @irPantllaPrincipal para ir al NavigationDrawer
     ****************************************************************************************/

    private void irPantallaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    /********************************************
     *                  Facebook
     ********************************************/


    private void handleFacebookAccessToken(AccessToken accessToken) {

        progressBar2.setVisibility(View.VISIBLE);
        botonIniciarFB.setVisibility(View.GONE);
        botonGoogle.setVisibility(View.GONE);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        //Toast.makeText(getApplicationContext(),String.valueOf(FacebookAuthProvider.PROVIDER_ID), Toast.LENGTH_LONG).show();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String facebookUserId="";

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
                    DatabaseReference currentUserDB = databaseReference.child(firebaseAuth.getCurrentUser().getUid());
                    currentUserDB.child("nombre").setValue(user.getDisplayName());
                    currentUserDB.child("correo").setValue(user.getEmail());


                    for(UserInfo profile : user.getProviderData()) {
                        // verifica si el id coincide en fb
                        if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                            facebookUserId = profile.getUid();
                        }
                    }

                    String photoUrll = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
                    currentUserDB.child("foto").setValue(photoUrll);
                    currentUserDB.child("proveedor").setValue(FacebookAuthProvider.PROVIDER_ID);

                }else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(getApplicationContext(),"El correo que está usando ya esta en uso , por favor use otro", Toast.LENGTH_LONG).show();
                    LoginManager.getInstance().logOut();
                }

                progressBar2.setVisibility(View.GONE);
                botonIniciarFB.setVisibility(View.VISIBLE);
                botonGoogle.setVisibility(View.VISIBLE);
            }
        });
    }

    /********************************************
     *                  Google
     ********************************************/

    private void iniciarSesion(){
        Intent signInIntent = mGoogleApiClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleGoogle(GoogleSignInAccount accessToken){

        progressBar2.setVisibility(View.VISIBLE);
        //loginButton.setVisibility(View.GONE);
        botonIniciarFB.setVisibility(View.GONE);
        botonGoogle.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(accessToken.getIdToken(),null);


        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
                    DatabaseReference currentUserDB = databaseReference.child(firebaseAuth.getCurrentUser().getUid());
                    currentUserDB.child("nombre").setValue(user.getDisplayName());
                    currentUserDB.child("correo").setValue(user.getEmail());
                    currentUserDB.child("foto").setValue(user.getPhotoUrl().toString());
                    currentUserDB.child("proveedor").setValue(GoogleAuthProvider.PROVIDER_ID.toString());

                }else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(getApplicationContext(),"El correo ya esta en uso , por favor use otro", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                }
                progressBar2.setVisibility(View.GONE);
                //loginButton.setVisibility(View.VISIBLE);
                botonIniciarFB.setVisibility(View.VISIBLE);
                botonGoogle.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        //Toast.makeText(getApplicationContext(),"La putamadre",Toast.LENGTH_LONG).show();
    }



    /********************************************
     *              Solo clicks :v
     ********************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.facebookView : loginButton.performClick(); break;
            case R.id.googleButton : iniciarSesion();
            case R.id.googleView : iniciarSesion(); break;
        }
    }
}
