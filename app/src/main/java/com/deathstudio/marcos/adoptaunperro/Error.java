package com.deathstudio.marcos.adoptaunperro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

public class Error extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout)findViewById(R.id.swipe);

        swipeView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
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
                            Intent i = new Intent(Error.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"NO ESTAS CONECTADO A INTERNET ", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, 3000);

            }
        });

    }

    public boolean existeConexionInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

}