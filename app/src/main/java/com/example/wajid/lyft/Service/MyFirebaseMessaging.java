package com.example.wajid.lyft.Service;

import android.content.Intent;

import com.example.wajid.lyft.CustommerCall;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static android.R.attr.name;

/**
 * Created by wajid on 17-Mar-18.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {
    public MyFirebaseMessaging() {
    }

    public void onMessageReceived(RemoteMessage remoteMessage){

        LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);
        Intent intent = new Intent(getBaseContext() , CustommerCall.class);
        intent.putExtra("lat",customer_location.latitude);
        intent.putExtra("lng",customer_location.longitude);
        intent.putExtra("customer",remoteMessage.getNotification().getTitle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
