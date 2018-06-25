package com.deathstudio.marcos.adoptaunperro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
//https://firebase.google.com/docs/functions/use-cases?hl=es-419


    public static final String TAG = "TOKEEEEN";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        String click_Action = remoteMessage.getNotification().getClickAction();
            Log.d(TAG,"SUA : "+ remoteMessage.getFrom());

        mostrarNotificaciones(click_Action,
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(),
                remoteMessage.getData().get("fotoPerro"),
                remoteMessage.getData().get("idUsuario"));

    }

        private void mostrarNotificaciones(String click_Action, String title, String body,String foto,String idUsuario){

        Intent i = new Intent(click_Action);
        i.putExtra("fotoPerro",foto);
        i.putExtra("idUsuario",idUsuario);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                .setSmallIcon(R.drawable.perro)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sonido_de_perro))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }


        notificationManager.notify(0,notificationBuilder.build());
    }
}
