package com.demoandroid.services;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://rovertv-apis.techroversolutions.com/";

    public static Retrofit retrofit = null;
    public static ApiClient apiClientInstance;
    public static ApiService apiService;

    public ApiClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static ApiClient getClientInstance() {
        if (apiClientInstance == null) {
            apiClientInstance = new ApiClient();
        }
        return apiClientInstance;
    }

    public static Map<String,String> getHeaders(boolean shouldAddHeader, Context context) {
        Map<String, String> headers = new HashMap<>();
        if (shouldAddHeader) {
            SharedPreferences sharedpreferences = context.getSharedPreferences("cred_prefs", Context.MODE_PRIVATE);
            headers.put("Token", sharedpreferences.getString("token",""));
        }
        return headers;
    }
}
