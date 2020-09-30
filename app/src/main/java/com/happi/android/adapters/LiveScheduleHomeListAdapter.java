package com.happi.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.LiveScheduleResponse;
import com.happi.android.models.ScheduleUpdatedModel;
import com.happi.android.utils.ConstantUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class LiveScheduleHomeListAdapter extends RecyclerView.Adapter<LiveScheduleHomeListAdapter.LiveHomeViewHolder> {
    private Context context;
    private List<LiveScheduleResponse.LiveScheduleModel> liveScheduleList;

    public LiveScheduleHomeListAdapter(Context context) {
        this.liveScheduleList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public LiveHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_schedule_home, null);
        return new LiveHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveHomeViewHolder holder, int position) {

        if (liveScheduleList.size() != 0) {
            holder.tv_schedule_title.setText(liveScheduleList.get(position).getVideo_title());

            String time = getScheduleItemTime(liveScheduleList.get(position),position);
            holder.tv_schedule_time.setText(time);

            boolean isTomorrow = IsScheduleForToday(liveScheduleList.get(position));
            if(isTomorrow){
                holder.tv_schedule_day.setText("Tomorrow");
            }else{
                holder.tv_schedule_day.setText("");
            }
            Glide.with(context)
                    .load(ConstantUtils.RELEASE_THUMBNAIL + liveScheduleList.get(position).getThumbnail())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .into(holder.iv_schedule_image);
        }
    }
    private boolean IsScheduleForToday(LiveScheduleResponse.LiveScheduleModel schedule){
        boolean isTomorrow = false;
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();
        Date finalStartDateTime = schedule.getStartDateTime();


        if(finalStartDateTime != null){

            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            String dateCurrent = sdfLocal.format(currentDate);
            String dateStart = sdfLocal.format(finalStartDateTime);
            if(!dateCurrent.equals(dateStart)){
                //schedule start is today or not
                isTomorrow = true;
            }

            /*if(finalStartDateTime.after(currentDate)){
                //schedule start is today or not
                isTomorrow = true;
            }*/
        }



        return isTomorrow;
    }
    private String getScheduleItemTime(LiveScheduleResponse.LiveScheduleModel itemSchedule, int position){
        String localTime = "";
        String startDateTime = itemSchedule.getStarttime();
        String endDateTime = itemSchedule.getEndtime();

        String startDateString = "";
        String endDateString = "";

        String startTimeString = "";
        String endTimeString = "";

        String startTime = "";
        String endTime = "";

        String finalStartTime = "";
        String finalEndTime = "";

        Date finalStartDateTime = null;
        Date finalEndDateTime = null;

        if(startDateTime.contains("T")){
            String[] start= startDateTime.split("T");
            if(start.length == 2){
                startDateString = start[0];
                startTimeString = start[1];
            }
        }
        if(endDateTime.contains("T")){
            String[] end= startDateTime.split("T");
            if(end.length == 2){
                endDateString = end[0];
                endTimeString = end[1];
            }
        }

       /* if(startTimeString.contains(".")){
            String[] startTimeOnly= startTimeString.split(".");
            if(startTimeOnly.length > 0){
                startTime = startTimeOnly[0];
            }
        }

        if(endTimeString.contains(".")){
            String[] endTimeOnly= endTimeString.split(".");
            if(endTimeOnly.length > 0){
                endTime = endTimeOnly[0];
            }
        }*/
        Log.d("liveschhmeadapter","pos: "+position+">> start-"+startDateString+"&"+startTimeString+"*"+startTime);
        Log.d("liveschhmeadapter","pos: "+position+">> end-"+endDateString+"&"+endTimeString+"*"+endTime);

        /*Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();*/

        SimpleDateFormat sdfYearTimeLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfYearTimeUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfYearTimeUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a", Locale.getDefault());

        try{
            //start
            String startString = startDateString+" "+startTimeString;
            Date parsedDateStart = sdfYearTimeUTC.parse(startString);
            finalStartTime = sdfTime.format(parsedDateStart);
            //finalStartDateTime = sdfYearTimeUTC.parse(startString);
            Date d1 = sdfYearTimeUTC.parse(startString);
            String s1 = sdfYearTimeLocal.format(d1);
            finalStartDateTime = sdfYearTimeLocal.parse(s1);
            //end
            String endString = endDateString+" "+endTimeString;
            Date parsedDateEnd = sdfYearTimeUTC.parse(endString);
            finalEndTime = sdfTime.format(parsedDateEnd);
            finalEndDateTime = sdfYearTimeUTC.parse(endString);
        }catch(Exception ex){
            Log.e("exception date","");
        }
        liveScheduleList.get(position).setStartDateTime(finalStartDateTime);
        liveScheduleList.get(position).setEndDateTime(finalEndDateTime);

        /*if(finalStartDateTime.after(currentDate)){
           //schedule start is today or not

        }*/

        localTime = finalStartTime +" - "+finalEndTime;

        return localTime;
    }

    @Override
    public int getItemCount() {
        return liveScheduleList.size();
    }

    public static class LiveHomeViewHolder extends RecyclerView.ViewHolder {
        TypefacedTextViewRegular tv_schedule_time;
        TypefacedTextViewBold tv_schedule_title;
        TypefacedTextViewBold tv_schedule_day;
        ImageView iv_schedule_image;

        public LiveHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_schedule_time = itemView.findViewById(R.id.tv_schedule_time);
            this.tv_schedule_title = itemView.findViewById(R.id.tv_schedule_title);
            this.iv_schedule_image = itemView.findViewById(R.id.iv_schedule_image);
            this.tv_schedule_day = itemView.findViewById(R.id.tv_schedule_day);

        }
    }

    public void clearAll() {
        liveScheduleList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<LiveScheduleResponse.LiveScheduleModel> moveResults) {
        for (LiveScheduleResponse.LiveScheduleModel result : moveResults) {
            add(result);
        }
    }

    public void add(LiveScheduleResponse.LiveScheduleModel r) {
        liveScheduleList.add(r);
        notifyItemInserted(liveScheduleList.size() - 1);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }
}
