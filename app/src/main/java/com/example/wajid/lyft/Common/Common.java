package com.example.wajid.lyft.Common;

import android.location.Location;

import com.example.wajid.lyft.Model.User;
import com.example.wajid.lyft.Remote.FCMClient;
import com.example.wajid.lyft.Remote.IFCMService;
import com.example.wajid.lyft.Remote.IGoogleAPI;
import com.example.wajid.lyft.Remote.RetrofitClient;

/**
 * Created by wajid on 13-Feb-18.
 */

public class Common {

    public static final String driver_tbl = "Driver";
    public static final String user_driver_thi = "DriverInformation";
    public static final String user_rider_tb1 = "RidersInformation";
    public static final String pickup_request_tb1 = "PickupRequest";
    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com/";
    public static final String token_tbl = "Tokens";

    public static User currentUser;

    public static Boolean alreadyExecuted = false;

    public static Location mLastLocation = null;

    public Common() {
    }

    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    /*public static RiderFCMService getRiderFCMService()
    {
        return FCMClient.getClient(fcmURL).create(RiderFCMService.class);
    }*/
}
