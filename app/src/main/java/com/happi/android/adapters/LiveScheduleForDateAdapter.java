package com.happi.android.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.happi.android.R;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ScheduleUpdatedModel;
import com.happi.android.utils.ConstantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.suke.widget.SwitchButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class LiveScheduleForDateAdapter extends RecyclerView.Adapter<LiveScheduleForDateAdapter.DateScheduleViewHolder> {

    private Context context;
    private List<ScheduleUpdatedModel> listSchedule;
    private onNotificationTriggerOn onNotificationTriggerOn;
    private onNotificationTriggerOff onNotificationTriggerOff;

    public LiveScheduleForDateAdapter(Context context, onNotificationTriggerOn onNotificationTriggerOn,
                                      onNotificationTriggerOff onNotificationTriggerOff ){
        this.context = context;
        this.onNotificationTriggerOn = onNotificationTriggerOn;
        this.onNotificationTriggerOff = onNotificationTriggerOff;
        this.listSchedule = new ArrayList<>();
    }


    @NonNull
    @Override
    public DateScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v  = LayoutInflater.from(context).inflate(R.layout.item_date_wise_schedule, null);
        return new DateScheduleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DateScheduleViewHolder dateScheduleViewHolder, int i) {
        if(listSchedule.size() > 0){
            dateScheduleViewHolder.tv_video_title_schedule.setText(listSchedule.get(i).getVideo_title());
            String time = getScheduleItemTime(listSchedule.get(i),i);
            dateScheduleViewHolder.iv_time_schedule_item.setText(time);
            Glide.with(context)
                    .load(ConstantUtils.THUMBNAIL_URL+listSchedule.get(i).getThumbnail())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .into(dateScheduleViewHolder.iv_schedule_item_logo);
            ArrayList<Integer> listOfIds = SharedPreferenceUtility.getNotificationIds();
            if(listOfIds.size()>0){
                if(listOfIds.contains(listSchedule.get(i).getUniqueid())){
                    dateScheduleViewHolder.sw_schedule.setChecked(true);
                }else{
                    dateScheduleViewHolder.sw_schedule.setChecked(false);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return listSchedule.size();
    }
    private String getTimeTzToLocal(String timetz, int index){

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
            listSchedule.get(index).setEpoch(d3.getTime());
            String d4 = sdf.format(d3);
            String d5 = sdf2.format(sdf.parse(d4));
            localTime = d5;

        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }



        return localTime;
    }
    private String getScheduleItemTime(ScheduleUpdatedModel itemSchedule, int position){
        String finalDate = itemSchedule.getStart_date_time();
        String localTime = getTimeTzToLocal(finalDate,position);

        return localTime;
    }

    public void addAll(List<ScheduleUpdatedModel> newList){
        for(ScheduleUpdatedModel model : newList){
            add(model);
        }
    }
    public void add(ScheduleUpdatedModel model){
        listSchedule.add(model);
        notifyItemInserted(listSchedule.size() - 1);
    }
    public void clearAll(){
        listSchedule.clear();
        notifyDataSetChanged();
    }
    public boolean isEmpty(){
        return getItemCount() == 0;
    }
    class DateScheduleViewHolder extends RecyclerView.ViewHolder{

        TypefacedTextViewBold tv_video_title_schedule;
        TypefacedTextViewRegular iv_time_schedule_item;
        ImageView iv_schedule_item_logo;
        SwitchButton sw_schedule;

        public DateScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_video_title_schedule = itemView.findViewById(R.id.tv_video_title_schedule);
            iv_time_schedule_item = itemView.findViewById(R.id.iv_time_schedule_item);
            iv_schedule_item_logo = itemView.findViewById(R.id.iv_schedule_item_logo);
            sw_schedule = itemView.findViewById(R.id.sw_schedule);

            sw_schedule.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked) {
                        onNotificationTriggerOn.onNotificationOn(listSchedule.get(getAdapterPosition()));
                    }else{
                        onNotificationTriggerOff.onNotificationOff(listSchedule.get(getAdapterPosition()));
                    }
                }
            });
        }
    }
    public interface onNotificationTriggerOn{
        void onNotificationOn(ScheduleUpdatedModel model);
    }
    public interface onNotificationTriggerOff{
        void onNotificationOff(ScheduleUpdatedModel model);
    }
}
