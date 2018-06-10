package com.deathstudio.marcos.adoptaunperro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.Configuracion.Configuracion;
import com.deathstudio.marcos.adoptaunperro.fragments.FragmentChatConsultas;
import com.deathstudio.marcos.adoptaunperro.fragments.FragmentoInicio;
import com.deathstudio.marcos.adoptaunperro.fragments.FragmentoPublicarAnuncio;
import com.deathstudio.marcos.adoptaunperro.pojo.Usuario;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /*@BindView(R.id.nombre)*/ TextView nameTextView;
    /*@BindView(R.id.correo)*/ TextView emailTextView;
    /*@BindView(R.id.imageView2)*/ CircleImageView photoImageView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Home");
        drawerConfig();
        seleccionarItem();
        obtenerDatosDeUsuario();
    }

    /*****************************************************************************
     *          Mostramos los datos del Usuario sino regresamos al Login
     *****************************************************************************/

    public void obtenerDatosDeUsuario(){
        final View header = navigationView.getHeaderView(0);
        nameTextView = header.findViewById(R.id.nombre);
        emailTextView = header.findViewById(R.id.correo);
        photoImageView = header.findViewById(R.id.imageView2);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("usuarios");

        if(user != null){
            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    nameTextView.setText(usuario.getNombre());
                    emailTextView.setText(usuario.getCorreo());
                    Glide.with(getApplicationContext())
                            .load(usuario.getFoto())
                            .into(photoImageView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            pantallaLoginScreen();
        }
    }

    /*****************************************
     *          Regresar al Login
     ****************************************/

    private void pantallaLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /*****************************************
     *          Finalizar la Sesión
     ****************************************/

    public void cerrarSesion(){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        pantallaLoginScreen();
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

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*****************************************
     *  Manejamos las vistas del nav_header
     ****************************************/

    public void mostrarVista(int id) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home){
            setTitle("Home");
            fragmentManager.beginTransaction().replace(R.id.content_fragment, new FragmentoInicio()).commit();
        }else if (id == R.id.nav_publicacion){
            setTitle("Nueva Publicacion");
            fragmentManager.beginTransaction().replace(R.id.content_fragment, new FragmentoPublicarAnuncio()).commit();
        }else if (id == R.id.nav_mis_publicaciones){
            setTitle("Mis Publicaciones");
            //fragmentManager.beginTransaction().replace(R.id.content_fragment, new FragmentoPublicarAnuncio()).commit();
        }else if (id == R.id.nav_chat){
            setTitle("Chat Consultas");
            fragmentManager.beginTransaction().replace(R.id.content_fragment, new FragmentChatConsultas()).commit();
        }else if (id == R.id.salir){
            cerrarSesion();
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    /***************************************************
     *          Métodos para Navigation Drawer
     **************************************************/

    public void drawerConfig(){
        drawer.addDrawerListener(new DrawerLayout.DrawerListener(){
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
                        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));}

                }catch(Exception e){
                    Log.e("Exception",e.getMessage());
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView){}

            @Override
            public void onDrawerClosed(@NonNull View drawerView){
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
                }catch(Exception e){
                    Log.e("Exception",e.getMessage());
                }
            }

            @Override
            public void onDrawerStateChanged(int newState){}
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
    }

    public void seleccionarItem(){
        if (!Configuracion.OPCION_MENU) {
            navigationView.setCheckedItem(R.id.nav_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_fragment, new FragmentoInicio()).commit();
        }else {
            navigationView.setCheckedItem(Configuracion.SELECT_NAV);
            mostrarVista(Configuracion.SELECT_NAV);
        }
    }
}
