package com.deathstudio.marcos.adoptaunperro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.Configuracion.Configuracion;
import com.deathstudio.marcos.adoptaunperro.fragments.FragmentoInicio;
import com.deathstudio.marcos.adoptaunperro.fragments.FragmentoPublicarAnuncio;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener{

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView uidTextView;
    private CircleImageView photoImageView;
    private ImageView foto;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                try {
                    //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                        // Do something for lollipop and above versions

                        Window window = getWindow();

                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                        // finally change the color to any color with transparency

                        window.setStatusBarColor(getResources().getColor(R.color.green));}

                } catch (Exception e) {



                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        // Do something for lollipop and above versions

                        Window window = getWindow();

                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                        // finally change the color again to dark
                        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                    }
                } catch (Exception e) {

                }

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (!Configuracion.OPCION_MENU) {
            navigationView.setCheckedItem(R.id.nav_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, new FragmentoInicio()).commit();
        }else {
            navigationView.setCheckedItem(Configuracion.SELECT_NAV);
            mostrarVista(Configuracion.SELECT_NAV);
        }

        final View header = navigationView.getHeaderView(0);



       /* Pair<View, String> pair = Pair.create(view.findViewById(R.id.fab), TRANSITION_FAB);
        ActivityOptionsCompat options;
        Activity act = MainActivity.this;
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(act, pair);*/



        nameTextView = (TextView) header.findViewById(R.id.nombre);
        emailTextView = (TextView) header.findViewById(R.id.correo);
        photoImageView = (CircleImageView) header.findViewById(R.id.imageView2);
        //foto = (ImageView) findViewById(R.id.foto);



        String facebookUserId="";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            for(UserInfo profile : user.getProviderData()) {
                // verifica si el id coincide en fb
                if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    facebookUserId = profile.getUid();
                }
            }

            String photoUrll = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";

            nameTextView.setText(name);
            emailTextView.setText(email);

            Glide.with(getApplicationContext())
                    .load(photoUrll)
                    .into(photoImageView);
            Toast.makeText(getApplicationContext(),"Facebook",Toast.LENGTH_LONG).show();

        }
        else {
            pantallaLoginScreen();

        }//https://firebase.google.com/docs/auth/android/account-linking?hl=es-419
    }

    @Override
    protected void onStart(){
        super.onStart();
        //firebaseAuth.addAuthStateListener(firebaseAuthListener);
   /*     OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            nameTextView.setText(account.getDisplayName());
            emailTextView.setText(account.getEmail());

            Glide.with(getApplicationContext())
                    .load(account.getPhotoUrl())
                    .into(photoImageView);
        }
    }

    private void pantallaLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        pantallaLoginScreen();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /*****************************************
     *          Métodos por default
     ****************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        mostrarVista(item.getItemId());
        return true;
    }

    /*****************************************
     *  Manejamos las vistas del nav_header
     ****************************************/



    public void mostrarVista(int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home){
            setTitle("Homeeee");
            fragmentManager.beginTransaction().replace(R.id.content_fragment, new FragmentoInicio()).commit();
        }else if (id == R.id.nav_camera){
            setTitle("Nueva Publicacion");
            fragmentManager.beginTransaction().replace(R.id.content_fragment, new FragmentoPublicarAnuncio()).commit();
        }else if (id == R.id.salir){
            cerrarSesion();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
