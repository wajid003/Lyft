package com.example.wajid.lyft.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.wajid.lyft.Common.Common;
import com.example.wajid.lyft.CustommerCall;
import com.example.wajid.lyft.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import static android.R.attr.name;

/**
 * Created by wajid on 17-Mar-18.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {

    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification().getTitle().equals("Arrived!")) {
            if(!Common.alreadyExecuted) {
                showArrivedNotification(remoteMessage.getNotification().getBody());
                Common.alreadyExecuted = true;
            }
        } else {
            try {
                LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(), LatLng.class);
                Intent intent = new Intent(getBaseContext(), CustommerCall.class);
                intent.putExtra("lat", customer_location.latitude);
                intent.putExtra("lng", customer_location.longitude);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("customer", remoteMessage.getNotification().getTitle());

                startActivity(intent);

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void showArrivedNotification(String body) {
        //only API 25 and below works
        //for above api create notification channel
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,new Intent(),PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Arrived!")
                .setContentText(body)
                .setContentIntent(contentIntent);
        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        manager.notify(1,builder.build());
    }
}
