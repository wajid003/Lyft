package com.example.wajid.lyft.Service;

import android.content.Intent;

import com.example.wajid.lyft.CustommerCall;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import static android.R.attr.name;

/**
 * Created by wajid on 17-Mar-18.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {


    public void onMessageRecieved(RemoteMessage remoteMessage){

        LatLng customer_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);
        Intent intent = new Intent(getBaseContext() , CustommerCall.class);
        intent.putExtra("lat",customer_location.latitude);
        intent.putExtra("lng",customer_location.longitude);

        startActivity(intent);
    }
}
