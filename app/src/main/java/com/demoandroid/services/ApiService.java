package com.demoandroid.services;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @POST("{dynamicPath}")
    @Headers("Content-Type: application/json")
    Call<Map<String,Object>> post(
            @Path(value = "dynamicPath",encoded = true) String dynamicPath,
            @HeaderMap Map<String,String> headerMap,
            @Body Map<String,Object> dataModel
    );

    @GET("{dynamicPath}")
    @Headers("Content-Type: application/json")
    Call<Map<String,Object>> get(
            @Path(value = "dynamicPath",encoded = true) String dynamicPath,
            @HeaderMap Map<String,String> headerMap
    );
}
