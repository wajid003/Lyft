package com.example.wajid.lyft;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;

import com.example.wajid.lyft.Common.Common;
import com.example.wajid.lyft.Helper.CustomInfoWindow;
import com.example.wajid.lyft.Model.Data;
import com.example.wajid.lyft.Model.FCMResponse;
import com.example.wajid.lyft.Model.Sender;
import com.example.wajid.lyft.Model.Token;
import com.example.wajid.lyft.Model.User;
import com.example.wajid.lyft.Remote.IFCMService;
import com.example.wajid.lyft.Remote.RiderFCMService;
import com.example.wajid.lyft.Rider_Common.Rider_Common;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.id.content;

public class Rider_Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    SupportMapFragment mapFragment;

    //Location
    private GoogleMap mMap;

    ImageView imgExpandable;
    BottomSheetRiderFragment nBottomSheet;
    Button btnPickupRequest;

    boolean isDriverFound=false;
    String driverId=" ";
    int radius = 1;   //1 km
    int distance = 1; // 3 km
    private static final int LIMIT = 3;

    //send Alert
    RiderFCMService mService;

    //Play Services
    private static  final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Activity Welcome;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private  static int DISPLACEMENT = 10;

    DatabaseReference drivers;
    GeoFire geoFire;

    Marker mUserMarker;
    Welcome welcome = new Welcome();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider__home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mService = Common.getRiderFCMService();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        imgExpandable = (ImageView) findViewById(R.id.imgExpandable);
        nBottomSheet = BottomSheetRiderFragment.newInstance("Rider bottom sheet");
        imgExpandable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nBottomSheet.show(getSupportFragmentManager(),nBottomSheet.getTag());
            }
        });

        btnPickupRequest = (Button)findViewById(R.id.btnPickupRaquest);
        btnPickupRequest.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){

                if(isDriverFound)
                    requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
                else 
                    sendRequestToDriver(driverId);
        }
    });

        setUpLocation();

        updateFirebaseToken();
    }

    private void updateFirebaseToken() {

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");

            Token token = new Token(FirebaseInstanceId.getInstance().getToken());
            tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(token);
        }

    private void sendRequestToDriver(String driverId) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);

        tokens.orderByKey().equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapshot.getValue(Token.class);

                            //Make raw Payload - convert LatLng to json
                            String json_lat_lng = new Gson().toJson(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                            Data data = new Data("Lyft",json_lat_lng); // send to driver
                            Sender content = new Sender(data,token.getToken());// send data to token

                            mService.sendMessage(content)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if(response.body().success == 1)
                                                Toast.makeText(Rider_Home.this, "Request Sent!", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(Rider_Home.this,"Failed !", Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void requestPickupHere(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference("PickupRequest");
        GeoFire geoFire = new GeoFire(dbRequest);
        geoFire.setLocation(uid,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));


        if(mUserMarker.isVisible())
            mUserMarker.remove();

        mUserMarker=
                mMap.addMarker(new MarkerOptions()
                        .title("Pickup Here")
                        .snippet("")
                        .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mUserMarker.showInfoWindow();
        btnPickupRequest.setText("Getting your DRIVER");

        findDriver();
    }

    private void findDriver() {

        {
            DatabaseReference drivers = FirebaseDatabase.getInstance().getInstance().getReference("Driver");
            GeoFire gfDrivers = new GeoFire(drivers);
            GeoQuery geoQuery = gfDrivers.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
            geoQuery.removeAllListeners();
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if (!isDriverFound) {
                        isDriverFound = true;
                        driverId = key;
                        btnPickupRequest.setText("CALL DRIVER");
                        Toast.makeText(Rider_Home.this, "" + key, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {
                    if (!isDriverFound) {
                        radius++;
                        findDriver();
                    }
                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }

            });


        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                            displayLocation();
                    }
                }
                break;
        }
    }

    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            // Requesting runtime permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }
        else
        {
            if(checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                    displayLocation();
            }
        }
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            //Adding Marker
            if (mUserMarker != null)
                mUserMarker.remove();//Remove previous marker
            mUserMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Your Location"));

            // Moving the Camera to the Position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

            LoadAllAvailableDriver();

        } else {
            Log.d("ERROR", "Cannot get your location");
        }
    }


    private void LoadAllAvailableDriver() {


        // load all available drivers

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference("Driver");
        GeoFire gf = new GeoFire(driverLocation);

        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {

                FirebaseDatabase.getInstance().getReference("Users")
                                .child(key)
                                 .addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(DataSnapshot dataSnapshot) {
                                         // because rider and user model is same
                                         User rider = dataSnapshot.getValue(User.class);


                                     //Add driver to map


                                         mMap.addMarker(new MarkerOptions()
                                                 .position(new LatLng(location.latitude,location.longitude))
                                                 .flat(true)
                                                 .title(rider.getName())
                                                 .snippet("Phone : "+rider.getPhone())
                                                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
                                     }

                                     @Override
                                     public void onCancelled(DatabaseError databaseError) {

                                     }
                                 });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if(distance<=LIMIT) //distance just find for 3 km
                {
                    distance++;
                    LoadAllAvailableDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rider__home, menu);
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
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

       mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            displayLocation();
           startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }
}
