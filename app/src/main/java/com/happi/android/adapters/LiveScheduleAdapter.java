package com.happi.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.happi.android.R;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewSemiBold;
import com.happi.android.models.ScheduleUpdatedModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LiveScheduleAdapter extends RecyclerView.Adapter<LiveScheduleAdapter.LiveViewHolder> {

    private List<ScheduleUpdatedModel> liveScheduleList;
    private Context context;
    private OnScheduleItemClick onScheduleItemClick;
    private Activity activity;

    public LiveScheduleAdapter( Context context, List<ScheduleUpdatedModel> liveScheduleList, OnScheduleItemClick onScheduleItemClick){
        this.liveScheduleList = liveScheduleList;
        this.context = context;
        this.onScheduleItemClick = onScheduleItemClick;

    }
    public LiveScheduleAdapter(Activity activity, Context context, OnScheduleItemClick onScheduleItemClick){

        this.context = context;
        this.onScheduleItemClick = onScheduleItemClick;
        this.activity = activity;
        this.liveScheduleList = new ArrayList<>();

    }

    @NonNull
    @Override
    public LiveViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_live_schedule, null);
        return new LiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveViewHolder liveViewHolder, int i) {
        if(liveScheduleList.size() > 0){
            if(liveScheduleList.get(i).isNext()){
                liveViewHolder.tv_next_label.setVisibility(View.VISIBLE);
            }else{
                liveViewHolder.tv_next_label.setVisibility(View.GONE);
            }

            String time = getScheduleItemTime(liveScheduleList.get(i));
//            try{
//                String[] timeObtained = time.split(" ");
//                Calendar today = Calendar.getInstance();
//                Date dateToday = today.getTime();
//                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateToday);
//                String[] arrToday = date.split(" ");
//                if(timeObtained.length != 0){
//                    if(arrToday[0].equalsIgnoreCase(timeObtained[0])){
//                        liveViewHolder.tv_schedule_title.setText(liveScheduleList.get(i).getVideo_title());
//                        liveViewHolder.tv_schedule_item_time.setText(timeObtained[1] +" "+ timeObtained[2]);
//                    }else{
//                        /*ScheduleUpdatedModel schMod = liveScheduleList.get(i);
//                        liveScheduleList.remove(schMod);
//                        notifyDataSetChanged();*/
//                        liveViewHolder.tv_schedule_title.setText(liveScheduleList.get(i).getVideo_title());
//                        liveViewHolder.tv_schedule_item_time.setText("Tomorrow "+timeObtained[1] +" "+ timeObtained[2]);
//                    }
//                }
//
//            }catch(Exception ex){
//                Log.e("DATE ERROR",""+ex.getMessage());
//            }
            liveViewHolder.tv_schedule_title.setText(liveScheduleList.get(i).getVideo_title());
            liveViewHolder.tv_schedule_item_time.setText(time);

          //  liveViewHolder.tv_schedule_item_time.setText(time);
        }

    }

    @Override
    public int getItemCount() {
        return liveScheduleList.size();
    }

    public class LiveViewHolder extends RecyclerView.ViewHolder{
        TextView tv_next_label;
        TypefacedTextViewSemiBold tv_schedule_title;
        TextView tv_schedule_item_time;

        public LiveViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_next_label = itemView.findViewById(R.id.tv_next_label);
            this.tv_schedule_title = itemView.findViewById(R.id.tv_schedule_title);
            this.tv_schedule_item_time = itemView.findViewById(R.id.tv_schedule_item_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onScheduleItemClick.onScheduleClicked(getAdapterPosition());
                }
            });
        }
    }


    private String getScheduleItemTime(ScheduleUpdatedModel itemSchedule){

        String finalDate = itemSchedule.getStart_date_time();
        String localTime = getTimeTzToLocal(finalDate);

      /*  String tzDate = getTimeZoneDate(finalDate);
        String utcDate = getTimeTimeZoneToUtc(tzDate);
        String localDate = getTimeUTCToLocal(utcDate);

        return localDate;*/
      return localTime;
    }

    public void clearAll(){
        liveScheduleList.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<ScheduleUpdatedModel> moveResults) {
        for (ScheduleUpdatedModel result : moveResults) {
            add(result);
        }
    }
    public void add(ScheduleUpdatedModel r) {
        liveScheduleList.add(r);
        notifyItemInserted(liveScheduleList.size() - 1);
    }
    public boolean isEmpty() {
        return getItemCount() == 0;
    }
    public interface OnScheduleItemClick{
        void onScheduleClicked(int adapterPosition);
    }


    private String getTimeTzToLocal(String timetz){

        String localTime = "";
        String tz = SharedPreferenceUtility.getChannelTimeZone();
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone t = TimeZone.getDefault();
        sdfLocal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdfTz = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfTz.setTimeZone(TimeZone.getTimeZone(tz));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a");

        try{

            Date d1 = sdfLocal.parse(timetz);
            String d2 = sdfLocal.format(d1);
            Date d3 = sdfTz.parse(d2);
            String d4 = sdf.format(d3);
            String d5 = sdf2.format(sdf.parse(d4));
            localTime = d5;

        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }



        return localTime;
    }


}
