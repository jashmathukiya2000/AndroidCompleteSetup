package com.demoandroid.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demoandroid.R;
import com.demoandroid.services.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EventListApdapter extends RecyclerView.Adapter<EventListApdapter.ViewHolder> {

    private final ArrayList eventList;

    public EventListApdapter(ArrayList listdata) {
        this.eventList = listdata;
        Log.e("LIST_ELEMS", "" + listdata);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.event_listing_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> obj = (Map<String, Object>) eventList.get(position);
        try {
            //set dates
            Date startData = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("startDate").toString());
            Date endData = new SimpleDateFormat("yyyy-MM-dd").parse(obj.get("startDate").toString());
            holder.startDateMonth.setText(DateFormat.format("MMM", startData));
            holder.startDateDay.setText(DateFormat.format("dd", startData));
            holder.endDateMonth.setText(DateFormat.format("MMM", endData));
            holder.endDateDay.setText(DateFormat.format("dd", endData));

            //set event texts
            holder.eventTitle.setText(obj.get("eventName").toString());
            GradientDrawable drawable = new GradientDrawable();
            String eventType = obj.get("eventType").toString();
            drawable.setColor(eventType.equals("GALLERY")?Color.parseColor("#fa05dd"):eventType.equals("EVENT")?Color.parseColor("#e3c040"):Color.parseColor("#0000ff"));
            drawable.setCornerRadius(10);
            holder.eventType.setBackground(drawable);
            holder.eventType.setText(eventType);

            //set duration
            double durationInSeconds = Double.parseDouble(obj.get("duration").toString());
            holder.durationText.setText("Duration of event: " + (int) ((durationInSeconds % 3600) / 60) + "m");

            //set priority
            double priority = Double.parseDouble(obj.get("priority").toString());
            holder.priorityIcon.setImageResource((priority <= 5) ? R.drawable.arrow_down_24 : (priority > 5 && priority <= 10) ? R.drawable.linear_scale_24 : R.drawable.arrow_up_24);
            holder.priorityIcon.setImageTintList(ColorStateList.valueOf((priority <= 5) ? Color.RED : (priority > 5 && priority <= 10) ? Color.YELLOW : Color.GREEN));
            holder.priorityRank.setText(""+(int) priority);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView startDateMonth;
        public TextView startDateDay;
        public TextView endDateMonth;
        public TextView endDateDay;
        public TextView eventTitle;
        public TextView eventType;
        public ImageView priorityIcon;
        public TextView priorityRank;
        public TextView durationText;

        public ViewHolder(View itemView) {
            super(itemView);
            this.startDateMonth = itemView.findViewById(R.id.start_date_month);
            this.startDateDay = itemView.findViewById(R.id.start_date_day);
            this.endDateMonth = itemView.findViewById(R.id.end_date_month);
            this.endDateDay = itemView.findViewById(R.id.end_date_day);
            this.eventTitle = itemView.findViewById(R.id.event_title);
            this.eventType = itemView.findViewById(R.id.event_type);
            this.priorityIcon = itemView.findViewById(R.id.priority_icon);
            this.durationText = itemView.findViewById(R.id.duration_text);
            this.priorityRank = itemView.findViewById(R.id.priority_rank);
        }
    }
}
