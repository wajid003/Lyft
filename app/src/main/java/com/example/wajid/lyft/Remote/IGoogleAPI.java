package com.example.wajid.lyft.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by wajid on 13-Feb-18.
 */

public interface IGoogleAPI {
    @GET
    Call<String> getPath (@Url String url);

}
