package com.example.wajid.lyft.Service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.wajid.lyft.CustommerCall;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * Created by wajid on 17-Mar-18.
 */

public class RIderMyFirebaseMessaging extends FirebaseMessagingService {
    public RIderMyFirebaseMessaging() {
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        //because this is outside of main thread, handler is used
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RIderMyFirebaseMessaging.this,""+remoteMessage.getNotification().getBody(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
