package com.happi.android.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.happi.android.R;
import com.happi.android.customviews.TypefacedTextViewBold;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.LiveScheduleResponse;
import com.happi.android.utils.AppUtils;
import com.happi.android.utils.ConstantUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class LiveScheduleInfoAdapter extends RecyclerView.Adapter<LiveScheduleInfoAdapter.LiveHomeInfoViewHolder> {
    private Context context;
    private List<LiveScheduleResponse.LiveScheduleModel> liveScheduleList;
    private int width = 0;

    public LiveScheduleInfoAdapter(Context context, int width) {
        this.liveScheduleList = new ArrayList<>();
        this.context = context;
        this.width = width;
    }

    @NonNull
    @Override
    public LiveHomeInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_live_schedule_info_home, null);
        return new LiveHomeInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LiveHomeInfoViewHolder holder, int position) {

        if (liveScheduleList.size() != 0) {
            holder.tv_schedule_title.setText(liveScheduleList.get(position).getVideo_title());

            String time = getScheduleItemTime(liveScheduleList.get(position),position);
            holder.tv_schedule_time.setText(time);

            //String day = getDayForScheduleItem(liveScheduleList.get(position),position);
            //holder.tv_schedule_day.setText(day);

            Glide.with(context)
                    .load(ConstantUtils.RELEASE_THUMBNAIL + liveScheduleList.get(position).getThumbnail())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .into(holder.iv_schedule_image);
        }
    }

    private String getDayForScheduleItem(LiveScheduleResponse.LiveScheduleModel schedule, int position){
        String status = "";

        if(position == 0){
            status = "Now Playing";
        }else{
            if(liveScheduleList.size() > 1){
                if(position == 1){
                    status = "Up Next";
                }else{
                    Date finalStartDateTime = schedule.getStartDateTime();
                    if(finalStartDateTime != null){
                        status = AppUtils.getDay(finalStartDateTime);
                    }else{
                        status = "";
                    }
                }
            }
        }
        return status;
    }



    private String getScheduleItemTime(LiveScheduleResponse.LiveScheduleModel itemSchedule, int position){
        String localTime = "";
        String startDateTime = itemSchedule.getStarttime();
        String endDateTime = itemSchedule.getEndtime();

        String startDateString = "";
        String endDateString = "";

        String startTimeString = "";
        String endTimeString = "";

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
            String[] end= endDateTime.split("T");
            if(end.length == 2){
                endDateString = end[0];
                endTimeString = end[1];
            }
        }




        SimpleDateFormat sdfYearTimeLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfYearTimeUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfYearTimeUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a", Locale.getDefault());

        try{
            //start
            String startString = startDateString+" "+startTimeString;

            Date d1 = sdfYearTimeUTC.parse(startString);
            String s1 = sdfYearTimeLocal.format(d1);
            finalStartTime = sdfTime.format(d1);
            finalStartDateTime = sdfYearTimeLocal.parse(s1);
            //end
            String endString = endDateString+" "+endTimeString;

            Date d2 = sdfYearTimeUTC.parse(endString);
            String s2 = sdfYearTimeLocal.format(d2);
            finalEndTime = sdfTime.format(d2);
            finalEndDateTime = sdfYearTimeLocal.parse(s2);
        }catch(Exception ex){
            Log.e("exception date","");
        }
        liveScheduleList.get(position).setStartDateTime(finalStartDateTime);
        liveScheduleList.get(position).setEndDateTime(finalEndDateTime);

        localTime = finalStartTime +" - "+finalEndTime;

        return localTime;
    }

    @Override
    public int getItemCount() {
        return liveScheduleList.size();
    }

    public class LiveHomeInfoViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_schedule_info_parent;
        FrameLayout fl_schedule_image;
        ImageView iv_schedule_image;
        TypefacedTextViewRegular tv_schedule_time;
        TypefacedTextViewBold tv_schedule_title;
        CardView cv_live_schedule;


        public LiveHomeInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_schedule_time = itemView.findViewById(R.id.tv_schedule_time);
            this.tv_schedule_title = itemView.findViewById(R.id.tv_schedule_title);
            this.iv_schedule_image = itemView.findViewById(R.id.iv_schedule_image);
            this.ll_schedule_info_parent = itemView.findViewById(R.id.ll_schedule_info_parent);
            this.fl_schedule_image = itemView.findViewById(R.id.fl_schedule_image);
            this.cv_live_schedule = itemView.findViewById(R.id.cv_live_schedule);
            //this.tv_schedule_day = itemView.findViewById(R.id.tv_schedule_day);

            int new_width = (width - (width/6))/3;
            int new_height = (3*(new_width-15))/2;

            int widthAdd = (int) context.getResources().getDimension(R.dimen.dimen_5dp);

            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(new_width, new_height);
            CardView.LayoutParams cl = new CardView.LayoutParams(new_width,new_height);
            cl.rightMargin = 15;
            this.ll_schedule_info_parent.setLayoutParams(fl);
            this.cv_live_schedule.setLayoutParams(cl);

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
