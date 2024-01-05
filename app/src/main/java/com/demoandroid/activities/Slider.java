package com.demoandroid.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;

import com.demoandroid.R;
import com.demoandroid.adapters.ViewPagerAdaptor;
import com.demoandroid.services.ApiClient;
import com.demoandroid.services.Utilities;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Slider extends AppCompatActivity {

    private int pageNumber = -1;
    ApiClient apiClient = new ApiClient();
    ArrayList contentObject = new ArrayList();
    ViewPager viewPager;

    ViewPagerAdaptor viewPagerAdaptor;
    TextClock clockTC;
    private TextView dateTimeDisplay;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        clockTC = findViewById(R.id.digitalClock);

        // format for our text clock
        clockTC.setFormat12Hour("hh:mm:ss a");

        //day and month
        dateTimeDisplay = (TextView) findViewById(R.id.date_textview);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
        dateTimeDisplay.setText(dateFormat.format(calendar.getTime()));

        //calling api every 5 mins
        Handler handler = new Handler();
        Runnable update = () -> {
            pageNumber = -1;
            getEventList();
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 0, 300000);
    }

    private void getEventList() {
        ++pageNumber;
        try {
            Map<String, Object> requestData = Utilities.jsonStringObjectToMap("{\n" +
                    "  \"filter\": {\n" +
                    "  },\n" +
                    "  \"page\": {\n" +
                    "    \"pageLimit\": 15,\n" +
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
                    if (response.body() == null || response.code() != 200) {
                        Utilities.alert(Utilities.States.FAIL, Slider.this, "Error code: " + response.code());
                        return;
                    }
                    Map<String, Object> statusObject = (Map<String, Object>) response.body().get("status");
                    if (statusObject.get("code").toString().equals("OK")) {
                        Map<String, Object> dataObject = (Map<String, Object>) response.body().get("data");
                        double totalElements = (double) dataObject.get("totalElements");
                        if (pageNumber == 0) {
                            boolean emptySlides = (contentObject.size() == 0);
                            contentObject = (ArrayList) dataObject.get("content");
                            if(emptySlides)initAdapter();
                        } else {
                            contentObject.addAll((ArrayList) dataObject.get("content"));
                        }
                        if (totalElements == contentObject.size()) {

                        }
                        viewPagerAdaptor.notifyDataSetChanged();
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

    private void initAdapter() {
        if (contentObject.size() == 0) {
            Utilities.alert(Utilities.States.WARNING, this, "No event available");
            return;
        }
        viewPagerAdaptor = new ViewPagerAdaptor(this, contentObject);
        viewPager = findViewById(R.id.slider_viewpager);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            @Override
            public void transformPage(@NonNull View page, float position) {
                int pageWidth = page.getWidth();
                int pageHeight = page.getHeight();

                if (position < -1) {
                    page.setAlpha(0);
                } else if (position <= 1) {
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if (position < 0) {
                        page.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        page.setTranslationX(-horzMargin + vertMargin / 2);
                    }
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                    page.setAlpha(MIN_ALPHA +
                            (scaleFactor - MIN_SCALE) /
                                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                } else {
                    page.setAlpha(0);
                }
            }
        });
        try {
            Field mScroller;
            Interpolator sInterpolator = new AccelerateInterpolator();
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerAdaptor.FixedSpeedScroller scroller = new ViewPagerAdaptor.FixedSpeedScroller(viewPager.getContext(), sInterpolator);
            scroller.setFixedDuration(2000);
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        viewPager.setAdapter(viewPagerAdaptor);
        Handler handler = new Handler();
        Runnable update = () -> {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == (contentObject.size() - 1)) {
                viewPager.setCurrentItem(0);
                return;
            }
            ++currentItem;
            viewPager.setCurrentItem(currentItem, true);
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 10000, 14000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}