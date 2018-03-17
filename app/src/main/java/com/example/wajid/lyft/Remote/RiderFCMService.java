package com.example.wajid.lyft.Remote;

import com.example.wajid.lyft.Model.FCMResponse;
import com.example.wajid.lyft.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by wajid on 17-Mar-18.
 */

public interface RiderFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA2GbsMoY:APA91bHBVWtVhehS-oOh1fOiHlROAJKwj2rG1aZq15719HN2d9pJpFTYMtOkBqSMir37Cg9iBxjdOx1Xw4ndWKXQhfx9VKNO3MnDaaNL1ItFQROSlBrOmT3CllvMkRyeVOlmORroEm8t"

    })

    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
