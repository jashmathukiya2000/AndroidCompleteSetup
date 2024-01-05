package com.demoandroid.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.demoandroid.R;
import com.demoandroid.services.ApiClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ViewPagerAdaptor extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private final ArrayList eventList;

    public ViewPagerAdaptor(Context context, ArrayList listdata) {
        this.eventList = listdata;
        this.context = context;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Map<String, Object> obj = (Map<String, Object>) eventList.get(position);
        Map<String, Object> nextObj;
        layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        View view = layoutInflater.inflate(R.layout.event_land_card, null);
        TextView eventTitleTextView = view.findViewById(R.id.event_slide_title);
        TextView eventContentTextView = view.findViewById(R.id.html_event_content);
        ImageView eventImageRef = view.findViewById(R.id.gallery_event_image);

        eventTitleTextView.setText(obj.get("eventName").toString());
        if (obj.get("eventType").toString().equals("GALLERY")) {
            Picasso.get().load("https://rovertv-apis.techroversolutions.com/core/file/view?fileKey="+obj.get("imageUrl").toString()).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(eventImageRef);

        } else if (obj.get("eventType").toString().equals("TEXT")) {
            eventContentTextView.setText(Html.fromHtml(obj.get("description").toString()));
        }

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        try {
            nextObj = (Map<String, Object>) eventList.get(position);
            Picasso.get().load("https://rovertv-apis.techroversolutions.com/core/file/view?fileKey="+nextObj.get("imageUrl").toString()).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE);
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    public static class FixedSpeedScroller extends Scroller {

        private int mDuration = 5000;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setFixedDuration(int i) {
            mDuration = i;
        }
    }
}
