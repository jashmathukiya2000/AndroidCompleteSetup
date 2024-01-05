package com.demoandroid.services;

import static java.util.stream.Collectors.toMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.compose.material.Colors;

import com.demoandroid.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utilities  {

    public static void loading(boolean isLoading, Activity activity,int loadingViewShowId,int viewToHideId){
        View loadingView = activity.findViewById(loadingViewShowId);
        View viewToHide = activity.findViewById(viewToHideId);
        loadingView.setVisibility((isLoading)?View.VISIBLE:View.GONE);
        viewToHide.setVisibility((isLoading)?View.GONE:View.VISIBLE);
    }

    public static void alert(States state,Activity activity,String msg){

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(state == States.SUCCESS?Color.GREEN:state == States.WARNING?activity.getResources().getColor(R.color.theme_regular):Color.RED);
        drawable.setCornerRadius(50);

        LinearLayout layout = new LinearLayout(activity.getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, // Width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height
        );
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackground(drawable);
//        layout.setBackgroundColor(state == States.SUCCESS?Color.GREEN:state == States.WARNING?activity.getResources().getColor(R.color.theme_regular):Color.RED);
        TextView textView = new TextView(activity.getApplicationContext());
        textView.setText(msg);
        textView.setTextColor(state == States.FAIL?Color.WHITE:Color.BLACK);
        textView.setTextSize(16);
        textView.setPadding(40, 20, 40, 20);
        layout.addView(textView);
        toast.setView(layout);
        toast.setGravity(isKeyboardOpen(activity)?Gravity.TOP:Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100); // Set the position of the Toast
        toast.show();
    }

    private static boolean isKeyboardOpen(Activity activity) {
        Rect r = new Rect();
        View rootView = activity.findViewById(android.R.id.content);
        rootView.getWindowVisibleDisplayFrame(r);
        int screenHeight = rootView.getHeight();
        int keypadHeight = screenHeight - r.bottom;
        return keypadHeight > screenHeight * 0.15;
    }

    public enum States{
        SUCCESS,
        WARNING,
        FAIL
    }

    public static Map<String, Object> jsonStringObjectToMap(String jsonString) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);
        Map<String, Object> resultMap = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                resultMap.put(key, jsonStringObjectToMap(String.valueOf(value)));
            } else if (value instanceof JSONArray) {
                resultMap.put(key, jsonArrayToList((JSONArray) value));
            } else {
                resultMap.put(key, value);
            }
        }

        return resultMap;
    }

    private static Object jsonArrayToList(JSONArray jsonArray) throws JSONException {
        int length = jsonArray.length();
        java.util.List<Object> list = new java.util.ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            Object element = jsonArray.get(i);
            if (element instanceof JSONObject) {
                list.add(jsonStringObjectToMap(String.valueOf(element)));
            } else if (element instanceof JSONArray) {
                list.add(jsonArrayToList((JSONArray) element));
            } else {
                list.add(element);
            }
        }

        return list;
    }

}

