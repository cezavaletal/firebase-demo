package com.deathstudio.marcos.adoptaunperro;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceService extends FirebaseInstanceIdService{

    public static final String TAG = "TOKEEEEN";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"TOKEN : "+ token);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
        DatabaseReference currentUserDB = databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserDB.child("token").setValue(FirebaseInstanceId.getInstance().getToken());
    }
}
