package com.example.wajid.lyft;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.example.wajid.lyft.Common.Common;
import com.example.wajid.lyft.Model.FCMResponse;
import com.example.wajid.lyft.Model.Notification;
import com.example.wajid.lyft.Model.Sender;
import com.example.wajid.lyft.Model.Token;
import com.example.wajid.lyft.Remote.IFCMService;
import com.example.wajid.lyft.Remote.IGoogleAPI;
import com.example.wajid.lyft.Remote.RiderFCMService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustommerCall extends AppCompatActivity {

    TextView txtTime,txtAddress,txtDistance;
    Button btnAccept,btnDecline;

    MediaPlayer mediaPlayer;

    IGoogleAPI mService;
    RiderFCMService mFCMService;

    String customerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);


        mService = Common.getGoogleAPI();
        mFCMService = Common.getRiderFCMService();

        //init
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        txtDistance = (TextView)findViewById(R.id.txtDistance);
        txtTime = (TextView)findViewById(R.id.txtTime);

        btnAccept = (Button)findViewById(R.id.btnAccept);
        btnDecline = (Button)findViewById(R.id.btnDecline);

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustommerCall.this,"Cancelled!",Toast.LENGTH_SHORT).show();
            }
        });

        mediaPlayer = MediaPlayer.create(this,R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if(getIntent()!=null)
        {
            double lat = getIntent().getDoubleExtra("lat",-1.0);
            double lng = getIntent().getDoubleExtra("lng",-1.0);
            customerId = getIntent().getStringExtra("customer");

            //get Direction code
            getDirection(lat,lng);
        }

    }

    private void getDirection(double lat,double lng) {

        String requestApi = null;
        try{
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "origin="+Common.mLastLocation.getLatitude()+","+Common.mLastLocation.getLongitude()+"&"+
                    "destination="+lat+" , "+lng+"&"+
                    "key="+getResources().getString(R.string.google_direction_api);

            Log.d("LYFT",requestApi);// print url for debug

            mService.getPath(requestApi)
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());

                                JSONArray routes = jsonObject.getJSONArray("routes");

                                //after get routes, just get first element of routes

                                JSONObject object = routes.getJSONObject(0);

                                JSONArray legs = object.getJSONArray("legs");

                                JSONObject legsObject = legs.getJSONObject(0);

                                //get distance
                                JSONObject distance = legsObject.getJSONObject("distance");
                                txtDistance.setText(distance.getString("text"));

                                //get time
                                JSONObject time = legsObject.getJSONObject("duration");
                                txtTime.setText(time.getString("text"));

                                //get address
                                String address = legsObject.getString("end_address");
                                txtAddress.setText(address);




                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(CustommerCall.this,""+t.getMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();
    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }
}

