package com.happi.android.customviews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.happi.android.R;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.models.ScheduleUpdatedModel;
import com.happi.android.utils.ConstantUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.suke.widget.SwitchButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;
import static com.bumptech.glide.request.RequestOptions.overrideOf;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;

public class LiveSchedulePopUpClass extends Dialog {

    public Context context;
    private ImageView iv_item_logo;
    private TypefacedTextViewRegular tv_item_title;
    private TypefacedTextViewRegular tv_item_time;
    private ScheduleUpdatedModel scheduleUpdatedModel;
    private onNotificationOn notificationOn;
    private onNotificationOff notificationOff;

    public LiveSchedulePopUpClass(@NonNull Context context, ScheduleUpdatedModel scheduleModel, onNotificationOn notificationOn, onNotificationOff notificationOff) {
        super(context);
        this.context = context;
        this.scheduleUpdatedModel = scheduleModel;
        this.notificationOn = notificationOn;
        this.notificationOff = notificationOff;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.live_schedule_popup);

        iv_item_logo = findViewById(R.id.iv_item_logo);
        tv_item_title = findViewById(R.id.tv_item_title);
        tv_item_time = findViewById(R.id.tv_item_time);
        SwitchButton sb_reminder = findViewById(R.id.sb_reminder);
        ArrayList<Integer> listOfIds = SharedPreferenceUtility.getNotificationIds();
        if(listOfIds.size()>0){
            if(listOfIds.contains(scheduleUpdatedModel.getUniqueid())){
                sb_reminder.setChecked(true);
            }else{
                sb_reminder.setChecked(false);
            }
        }
        sb_reminder.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked){
                    notificationOn.notificationOn(scheduleUpdatedModel);
                }else{
                    notificationOff.notificationOff(scheduleUpdatedModel);
                }
            }
        });
        setContent(scheduleUpdatedModel);
    }
    private void setContent(ScheduleUpdatedModel model){
        Glide.with(context)
                .load(ConstantUtils.THUMBNAIL_URL+model.getThumbnail())
                .error(Glide.with(context)
                        .load(ContextCompat.getDrawable(context, R.drawable.ic_placeholder)))
                .apply(placeholderOf(R.drawable.ic_placeholder))
                .apply(diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .apply(overrideOf(400,600))
                .into(iv_item_logo);
        tv_item_title.setText(model.getVideo_title());
        String time = getScheduleItemTime(model);
        tv_item_time.setText(time);

    }
    private String getScheduleItemTime(ScheduleUpdatedModel itemSchedule){

        String finalDate = itemSchedule.getStart_date_time();
        String localTime = getTimeTzToLocal(finalDate);

        return localTime;
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
    public interface onNotificationOn{
        void notificationOn(ScheduleUpdatedModel scheduleUpdatedModel);
    }
    public interface onNotificationOff{
        void notificationOff(ScheduleUpdatedModel scheduleUpdatedModel);
    }
}
