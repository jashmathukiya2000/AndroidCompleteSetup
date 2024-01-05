package com.demoandroid.activities;

import static java.util.stream.Collectors.toMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.demoandroid.R;
import com.demoandroid.adapters.EventListApdapter;
import com.demoandroid.services.ApiClient;
import com.demoandroid.services.Utilities;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private int pageNumber = -1;
    ApiClient apiClient = new ApiClient();
    RecyclerView recyclerView;
    ArrayList contentObject;
    EventListApdapter adapter;
    RecyclerView.OnScrollListener onScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        recyclerView = (RecyclerView) findViewById(R.id.event_list_view);
        getEventList();
    }

    private void getEventList() {
        ++pageNumber;
        try {
            Map<String, Object> requestData = Utilities.jsonStringObjectToMap("{\n" +
                    "  \"filter\": {\n" +
                    "  },\n" +
                    "  \"page\": {\n" +
                    "    \"pageLimit\": 5,\n" +
                    "    \"pageNumber\": " + pageNumber + "\n" +
                    "  },\n" +
                    "  \"sort\": {\n" +
                    "    \"orderBy\": \"ASC\",\n" +
                    "    \"sortBy\": \"Event_Name\"\n" +
                    "  }\n" +
                    "}");
            Log.e("requestData", "" + requestData.get("page"));
            apiClient.getClientInstance().apiService.post("core/event/getEvents", apiClient.getHeaders(true, getApplicationContext()), requestData).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if(response.body() == null || response.code() != 200) {
                        Utilities.alert(Utilities.States.FAIL,HomeActivity.this,"Error code: "+response.code());
                        return;
                    }
                    Map<String, Object> statusObject = (Map<String, Object>) response.body().get("status");
                    if (statusObject.get("code").toString().equals("OK")) {
                        Map<String, Object> dataObject = (Map<String, Object>) response.body().get("data");
                        double totalElements = (double) dataObject.get("totalElements");
                        if (pageNumber == 0) {
                            contentObject = (ArrayList) dataObject.get("content");
                            initAdapter();
                        } else {
                            contentObject.addAll((ArrayList) dataObject.get("content"));
                        }
                        adapter.notifyDataSetChanged();
                        if(totalElements == contentObject.size()){
                            recyclerView.removeOnScrollListener(onScrollListener);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initScrollListener() {
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == contentObject.size() - 1) {
                    getEventList();
                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }
    private void initAdapter() {
        adapter = new EventListApdapter(contentObject);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setAdapter(adapter);
        initScrollListener();
    }
}