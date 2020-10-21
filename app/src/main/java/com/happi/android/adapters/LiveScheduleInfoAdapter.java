package com.happi.android.adapters;

import android.app.ActionBar;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import java.util.Calendar;
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
            //holder.tv_schedule_title.setText(liveScheduleList.get(position).getVideo_title());

            String time = getScheduleItemTime(liveScheduleList.get(position), position);
            holder.tv_schedule_time.setText(time);


            // String day = getDayForScheduleItem(liveScheduleList.get(position));
//            String status = getStatus(day, liveScheduleList.get(position).getStartDateTime(),
//                    liveScheduleList.get(position).getEndDateTime(), position);

            String status = getDay(liveScheduleList.get(position).getStartDateTime(),
                    liveScheduleList.get(position).getEndDateTime(),
                    position);
            holder.tv_schedule_status.setText(status);

            Glide.with(context)
                    .load(ConstantUtils.THUMBNAIL_URL + liveScheduleList.get(position).getThumbnail())
                    .error(Glide.with(context)
                            .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                    .apply(placeholderOf(R.drawable.ic_placeholder))
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                    .into(holder.iv_schedule_image);


            if(liveScheduleList.get(position).getVideo_title() == null || liveScheduleList.get(position).getVideo_title().isEmpty()){
                holder.tv_video_title.setText("");
                holder.tv_video_title.setVisibility(View.INVISIBLE);
            }else{
                holder.tv_video_title.setText(liveScheduleList.get(position).getVideo_title().trim());
                holder.tv_video_title.setVisibility(View.VISIBLE);
            }

            if(liveScheduleList.get(position).getPartner_name() == null || liveScheduleList.get(position).getPartner_name().isEmpty()){
                holder.tv_partner_title.setText("");
                holder.tv_partner_title.setVisibility(View.INVISIBLE);
            }else{
                holder.tv_partner_title.setText(liveScheduleList.get(position).getPartner_name().trim());
                holder.tv_partner_title.setVisibility(View.VISIBLE);
            }

        }
    }

    private String getStatus(String day, Date finalStartDateTime, Date finalEndDateTime, int position) {
        String dayStatus = "";

        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();

        if (day.equalsIgnoreCase("Today")) {
            if ((currentDate.after(finalStartDateTime) && currentDate.before(finalEndDateTime)) ||
                    (currentDate.equals(finalStartDateTime))) {

                dayStatus = "Now Playing";
                liveScheduleList.get(position).setLive(true);

            } else {
                if (position > 1) {
                    int prev = --position;
                    if (liveScheduleList.get(prev).isLive()) {
                        dayStatus = "Up Next";
                    }
                }
            }
        }

        return dayStatus;

    }

    private String getDay(Date finalStartDateTime, Date finalEndDateTime, int position) {
        String status = "";

        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();

        if (finalStartDateTime != null && finalEndDateTime != null) {

            if ((currentDate.after(finalStartDateTime) && currentDate.before(finalEndDateTime)) ||
                    (currentDate.equals(finalStartDateTime))) {
                //status = "Now Playing";
                //liveScheduleList.get(position).setLive(true);
                status = "";

            } else {
                status = getDayForScheduleItem(liveScheduleList.get(position));

                if (status.equalsIgnoreCase("Today")) {
                    /*//uncomment for up next
                    if (position > 0) {
                        int previous = position - 1;
                        if (liveScheduleList.get(previous).isLive()) {
                            status = "Up Next";
                        }
                    }*/
                    status = "";
                }else{
                    SimpleDateFormat sdfDate = new SimpleDateFormat("MM-dd-yy",Locale.getDefault());
                   // SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM, yyyy",Locale.getDefault());
                    status = sdfDate.format(finalStartDateTime);
                }

            }

        }

        return status;
    }

    private String getDayForScheduleItem(LiveScheduleResponse.LiveScheduleModel schedule) {
        String status = "";

//        if(position == 0){
//            status = "Now Playing";
//        }else{
//            if(liveScheduleList.size() > 1){
//                if(position == 1){
//                    status = "Up Next";
//                }else{
//                    Date finalStartDateTime = schedule.getStartDateTime();
//                    if(finalStartDateTime != null){
//                        status = AppUtils.getDay(finalStartDateTime);
//                    }else{
//                        status = "";
//                    }
//                }
//            }
//        }

        Date finalStartDateTime = schedule.getStartDateTime();
        Date finalEndDateTime = schedule.getEndDateTime();
        if (finalStartDateTime != null && finalEndDateTime != null) {
            status = AppUtils.getDay(finalStartDateTime, finalEndDateTime);
        } else {
            status = "";
        }
        return status;
    }


    private String getScheduleItemTime(LiveScheduleResponse.LiveScheduleModel itemSchedule, int position) {
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

        if (startDateTime.contains("T")) {
            String[] start = startDateTime.split("T");
            if (start.length == 2) {
                startDateString = start[0];
                startTimeString = start[1];
            }
        }
        if (endDateTime.contains("T")) {
            String[] end = endDateTime.split("T");
            if (end.length == 2) {
                endDateString = end[0];
                endTimeString = end[1];
            }
        }


        SimpleDateFormat sdfYearTimeLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat sdfYearTimeUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfYearTimeUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a", Locale.getDefault());

        try {
            //start
            String startString = startDateString + " " + startTimeString;

            Date d1 = sdfYearTimeUTC.parse(startString);
            String s1 = sdfYearTimeLocal.format(d1);
            finalStartTime = sdfTime.format(d1);
            finalStartDateTime = sdfYearTimeLocal.parse(s1);
            //end
            String endString = endDateString + " " + endTimeString;

            Date d2 = sdfYearTimeUTC.parse(endString);
            String s2 = sdfYearTimeLocal.format(d2);
            finalEndTime = sdfTime.format(d2);
            finalEndDateTime = sdfYearTimeLocal.parse(s2);
        } catch (Exception ex) {
            Log.e("exception date", "");
        }
        liveScheduleList.get(position).setStartDateTime(finalStartDateTime);
        liveScheduleList.get(position).setEndDateTime(finalEndDateTime);

        localTime = finalStartTime + " - " + finalEndTime;

        return localTime;
    }

    @Override
    public int getItemCount() {
        return liveScheduleList.size();
    }

    public class LiveHomeInfoViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout ll_schedule_info_parent;
        FrameLayout fl_schedule_image;
        ImageView iv_schedule_image;
        TypefacedTextViewRegular tv_schedule_time;
        TypefacedTextViewBold tv_schedule_status;
        TypefacedTextViewRegular tv_schedule_title;
        CardView cv_live_schedule;

        LinearLayout ll_schedule_image_parent;
        LinearLayout ll_title_info;
        TypefacedTextViewRegular tv_partner_title;
        TypefacedTextViewRegular tv_video_title;





        public LiveHomeInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_schedule_time = itemView.findViewById(R.id.tv_schedule_time);
            this.tv_schedule_title = itemView.findViewById(R.id.tv_schedule_title);
            this.iv_schedule_image = itemView.findViewById(R.id.iv_schedule_image);
            this.ll_schedule_info_parent = itemView.findViewById(R.id.ll_schedule_info_parent);
            this.fl_schedule_image = itemView.findViewById(R.id.fl_schedule_image);
            this.cv_live_schedule = itemView.findViewById(R.id.cv_live_schedule);
            this.tv_schedule_status = itemView.findViewById(R.id.tv_schedule_status);

            this.ll_schedule_image_parent = itemView.findViewById(R.id.ll_schedule_image_parent);
            this.ll_title_info = itemView.findViewById(R.id.ll_title_info);
            this.tv_partner_title = itemView.findViewById(R.id.tv_partner_title);
            this.tv_video_title = itemView.findViewById(R.id.tv_video_title);

            this.tv_schedule_title.setVisibility(View.GONE);

            int new_width = (width - (width / 6)) / 3;
            int new_height = (3 * (new_width - 15)) / 2;

            //int heightAdd = (int) ((new_height)/3);
            //int heightAdd = (int) ((2 * new_height)/3);
            int heightAdd = (int) context.getResources().getDimension(R.dimen.dimen_55dp);
            Log.e("LIVESCHEDULE",": w:"+new_width);
            Log.e("LIVESCHEDULE",": h:"+new_height);
            Log.e("LIVESCHEDULE",": ha:"+heightAdd);

            /*FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(new_width, new_height+heightAdd);
            this.ll_schedule_info_parent.setLayoutParams(fl);

            CardView.LayoutParams cl = new CardView.LayoutParams(new_width, new_height+heightAdd);
            cl.rightMargin = 15;
            this.cv_live_schedule.setLayoutParams(cl);

            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(new_width, new_height);
            this.ll_schedule_image_parent.setLayoutParams(rl);*/



            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(new_width, new_height);
            this.ll_schedule_image_parent.setLayoutParams(rl);

            FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(new_width, FrameLayout.LayoutParams.WRAP_CONTENT);
            this.ll_schedule_info_parent.setLayoutParams(fl);

            CardView.LayoutParams cl = new CardView.LayoutParams(new_width, CardView.LayoutParams.WRAP_CONTENT);
            cl.rightMargin = 15;
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
