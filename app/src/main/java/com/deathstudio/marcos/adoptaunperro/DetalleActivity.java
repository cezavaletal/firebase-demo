package com.deathstudio.marcos.adoptaunperro;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.deathstudio.marcos.adoptaunperro.adapter.AdaptadorViewPager;
import com.deathstudio.marcos.adoptaunperro.fragments.FragmentDetallePerro;
import com.deathstudio.marcos.adoptaunperro.fragments.FragmentDetallePublicador;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetalleActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @BindView(R.id.imagenPerro) ImageView imagenPerro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Bundle bundle = getIntent().getExtras();

        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager,bundle);

        //tabLayout = findViewById(R.id.tabLayout);
        //tabLayout.setupWithViewPager(viewPager);




        Glide.with(this)
                .load(bundle.getString("fotoPerro"))
                .into(imagenPerro);
    }

    private void setupViewPager(ViewPager viewPager,Bundle bundle) {
        AdaptadorViewPager adapter = new AdaptadorViewPager(getSupportFragmentManager());
        Fragment fragmentDetallePublicador = new FragmentDetallePublicador();
        fragmentDetallePublicador.setArguments(bundle);

        adapter.addFragment( "Ficha Personal",new FragmentDetallePerro());
        adapter.addFragment( "Detalles del Publicador",fragmentDetallePublicador);
        viewPager.setAdapter(adapter);
    }
}
