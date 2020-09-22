package com.happi.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.happi.android.adapters.LiveScheduleDateAdapter;
import com.happi.android.adapters.LiveScheduleForDateAdapter;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.NotificationTriggerActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.SliderLayoutManager;
import com.happi.android.models.ScheduleUpdatedModel;
import com.happi.android.webservice.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LiveScheduleActivity extends BaseActivity implements LiveScheduleDateAdapter.onItemClicked,
        LiveScheduleForDateAdapter.onNotificationTriggerOn, LiveScheduleForDateAdapter.onNotificationTriggerOff,
        SliderLayoutManager.OnDateSelectedListener{

    private RecyclerView rv_date_list;
    private LiveScheduleDateAdapter liveScheduleDateAdapter;
    private  ArrayList<String> dateList;
    private String CHANNEL_ID = "";
    private CompositeDisposable compositeDisposable;
    private RecyclerView rv_schedule_list_for_date;
    private List<ScheduleUpdatedModel> scheduleList;
    private LiveScheduleForDateAdapter liveScheduleForDateAdapter;
    private RelativeLayout rl_error;
    private ImageView iv_error;
    private TextView tv_error;
    private ProgressDialog progressDialog;
    private int selectedDateposition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_schedule);

        compositeDisposable = new CompositeDisposable();

        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);


        if( getIntent() != null){
            if(!getIntent().getStringExtra("channel_id").isEmpty()){
                CHANNEL_ID = getIntent().getStringExtra("channel_id");
            }else{
                CHANNEL_ID = String.valueOf(SharedPreferenceUtility.getChannelId());
            }
           if(getIntent().getSerializableExtra("schedule_list") != null){
               scheduleList =(List<ScheduleUpdatedModel>) getIntent().getSerializableExtra("schedule_list");
           }else{
               scheduleList = new ArrayList<>();
           }
        }


        ImageView iv_back = findViewById(R.id.iv_back);
        rv_date_list = findViewById(R.id.rv_date_list);
        dateList = new ArrayList<>();
        liveScheduleDateAdapter = new LiveScheduleDateAdapter(this, this::onDateItemClicked );
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int padding = width / 2 - dpToPx(this, 52);
        rv_date_list.setPadding(padding, -8, padding, 0);
        rv_date_list.setLayoutManager(new SliderLayoutManager(LiveScheduleActivity.this, LinearLayoutManager.HORIZONTAL,false, this::onDateSelected));
      //  rv_date_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_date_list.setAdapter(liveScheduleDateAdapter);

        rv_schedule_list_for_date = findViewById(R.id.rv_schedule_list_for_date);
       // scheduleList = new ArrayList<>();
        liveScheduleForDateAdapter = new LiveScheduleForDateAdapter(this, this::onNotificationOn, this::onNotificationOff);
        rv_schedule_list_for_date.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_schedule_list_for_date.setAdapter(liveScheduleForDateAdapter);

        rl_error = findViewById(R.id.rl_error);
        iv_error = findViewById(R.id.iv_error);
        tv_error = findViewById(R.id.tv_error);

      //  FEApplication.setIsDatePickerClicked(false);

        showProgressDialog();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
              //  LiveScheduleActivity.super.onBackPressed();
            }
        });
        Date selectedDate = Calendar.getInstance().getTime();
        getDatesForScrollView(selectedDate);
        updateSchedule(scheduleList);

    }
    private void onBack(){
        //Intent intentChannel = new Intent(LiveScheduleActivity.this, ChannelHomeActivity.class);
        Intent intentChannel = new Intent(LiveScheduleActivity.this, ChannelLivePlayerActivity.class);
        startActivity(intentChannel);
        LiveScheduleActivity.super.finish();
    }
    public String getDate(Date current){
        String today = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            today = sdf.format(current);
        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }


        return today;
    }
    public String getDateFromLocalToTimezone(String local){
        String tz = SharedPreferenceUtility.getChannelTimeZone();
        String tzDate = "";
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone t = TimeZone.getDefault();
        sdfLocal.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdfTZ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfTZ.setTimeZone(TimeZone.getTimeZone(tz));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try{
            Date d1 = sdf.parse(local);
            String d2 = sdfTZ.format(d1);
            Date d3 = sdfLocal.parse(d2);
            tzDate = sdf.format(d3);

        }catch(Exception ex){
            Log.e("DATE ERROR",""+ex.getMessage());
        }
        return tzDate;
    }
    private void getScheduleForSelectedDate(Date dateSelected){

       // Date utcDate = getTimeLocalToUTC(dateSelected);
       // String dateForTimeZone = getTimeUtcToTimeZone(utcDate);

        String today = getDate(dateSelected);
        String start = "00:00:00";
        String end = "23:59:59";
        String startOfSelectedDate = today +" "+ start;
        String endOfSelectedDate = today + " "+ end;

        String tzDateStart = getDateFromLocalToTimezone(startOfSelectedDate);
        String tzDateEnd = getDateFromLocalToTimezone(endOfSelectedDate);

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable scheduleDisposable = usersService.getScheduleUpdated(HappiApplication.getAppToken(),SharedPreferenceUtility.getPublisher_id(),
                CHANNEL_ID, tzDateStart,tzDateEnd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scheduleUpdatedResponseModel -> {
                    if(scheduleUpdatedResponseModel.getData().size() != 0){
                        List<ScheduleUpdatedModel> list = scheduleUpdatedResponseModel.getData();
                       // updateScheduleForDate(list, dateForTimeZone);
                        updateSchedule(list);
                    }else{
                        displayErrorLayout("Sorry, no Schedule is available for this date.");
                    }

                },throwable -> {
                    dismissProgressDialog();
                    Toast.makeText(LiveScheduleActivity.this, "Sorry, Please try again after sometime.", Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(scheduleDisposable);
    }
    private void showProgressDialog(){
        rv_schedule_list_for_date.setVisibility(View.GONE);
       if(!progressDialog.isShowing()){
           progressDialog.show();
       }
    }
    private void dismissProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    private void displayErrorLayout(String message){
        dismissProgressDialog();
        rv_schedule_list_for_date.setVisibility(View.GONE);

        rl_error.setVisibility(View.VISIBLE);
        tv_error.setText(message);
        tv_error.setVisibility(View.VISIBLE);
        iv_error.setVisibility(View.VISIBLE);
    }
    private void hideErrorLayout(){
        rl_error.setVisibility(View.GONE);
        tv_error.setVisibility(View.GONE);
        iv_error.setVisibility(View.GONE);
    }
    private void updateScheduleForDate(List<ScheduleUpdatedModel> list, String timeInTZ){
        Date dateTz = getDateTimeTZ(timeInTZ);
        Calendar currentCal = Calendar.getInstance();
        Date current = currentCal.getTime();
        if(!current.after(dateTz) && !current.before(dateTz)){
            updateSchedule(scheduleList);
        }else{
            updateSchedule(list);
        }
    }
    private void updateSchedule(List<ScheduleUpdatedModel> scheduleList1){
        hideErrorLayout();
        showProgressDialog();
        rv_schedule_list_for_date.setVisibility(View.VISIBLE);


        liveScheduleForDateAdapter.clearAll();
        liveScheduleForDateAdapter.addAll(scheduleList1);
        liveScheduleForDateAdapter.notifyDataSetChanged();

        if(liveScheduleForDateAdapter.isEmpty()){
            rv_schedule_list_for_date.setVisibility(View.GONE);
        }
        dismissProgressDialog();
        HappiApplication.setIsDatePickerClicked(false);
    }
    private void getDatesForScrollView(Date date){
        int count = 1;
        Date incDate = null;
        String dateString = "";
        ArrayList<String> dateArray = new ArrayList<>();
        String FirstDateInString = getDateInString(date);
        dateArray.add(FirstDateInString);
        while(count<5){
            incDate = incrementDateByOneDay(date);
            dateString = getDateInString(incDate);
            dateArray.add(dateString);
            date = incDate;
            count++;
        }
        if(dateArray.size() != 0){
            dateList.addAll(dateArray);
            updateDateList(dateArray);
        }
    }
    private String getDateInString(Date dateToConvert){
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        date = sdf.format(dateToConvert);
        return date;
    }
    private Date incrementDateByOneDay(Date givenDate){
        Date incDate = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(givenDate);
        calendar.add(Calendar.DATE,1);
        incDate = calendar.getTime();
        return incDate;
    }
    private void updateDateList(ArrayList<String> arrayOfDate){
        ArrayList<String> dateList1 = arrayOfDate;
        liveScheduleDateAdapter.clearAll();
        liveScheduleDateAdapter.addAll(dateList1);
        liveScheduleDateAdapter.notifyDataSetChanged();

        if(liveScheduleDateAdapter.isEmpty()){
            rv_date_list.setVisibility(View.GONE);
        }
    }
    public Date getTimeLocalToUTC(Date local){

        Date utcDate = null;
        SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));

        try{
            utcDate = sdfLocal.parse(sdfUtc.format(local));
            //  utcDate = sdfUtc.parse(sdfLocal.format(localDate));
        }catch(Exception ex){
            Log.e("DATE ERROR","");
        }
        return utcDate;
    }

    public Date getDateTimeTZ(String dateTZ){

        Date tzDateD = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        try{
            Date dt3 = sdf.parse(dateTZ);
            tzDateD = dt3;
        }catch(Exception ex){
            Log.e("DATE ERROR","");
        }
        return tzDateD;
    }

    @Override
    public void onDateItemClicked(int adapterPosition) {
        showProgressDialog();
        if(adapterPosition>=0){
            rv_date_list.smoothScrollToPosition(adapterPosition);
        }

       // FEApplication.setIsDatePickerClicked(true);
        dateSelected(adapterPosition);
       // FEApplication.setIsDatePickerClicked(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeDisposables(compositeDisposable);
    }
    private void disposeDisposables(Disposable... disposables){
        for(Disposable disposable : disposables){
            if((disposable != null) && !(disposable.isDisposed())){
                disposable.dispose();
            }
        }
    }


    //xoxo


    @Override
    public void onNotificationOn(ScheduleUpdatedModel model) {
        long epoch = 0L;
        String videoTitle = "";
        int channelId = -1;
        int uniqueId = 0;
        epoch = model.getEpoch();
        videoTitle = model.getVideo_title();
        channelId = model.getChannel_id();
        uniqueId = model.getUniqueid();
//        Calendar curr = Calendar.getInstance();
//        curr.add(Calendar.MINUTE,3);
//        epoch = curr.getTime().getTime();
        NotificationTriggerActivity.sendNotifications(epoch, videoTitle, channelId, uniqueId,LiveScheduleActivity.this);
        ArrayList<Integer> listOfIds = SharedPreferenceUtility.getNotificationIds();
        listOfIds.add(uniqueId);
        SharedPreferenceUtility.setNotificationIds(listOfIds);
    }

    @Override
    public void onNotificationOff(ScheduleUpdatedModel model) {
        int unique_id = 0;
        unique_id = model.getUniqueid();
        NotificationTriggerActivity.clearNotification(unique_id, LiveScheduleActivity.this);
        ArrayList<Integer> listOfIds = SharedPreferenceUtility.getNotificationIds();
        int index = listOfIds.indexOf(unique_id);
        if(index != -1) {
            listOfIds.remove(index);
            SharedPreferenceUtility.setNotificationIds(listOfIds);
        }
    }

    @Override
    public void onDateSelected(int layoutPosition) {
        if(layoutPosition < dateList.size() && layoutPosition >= 0 && (selectedDateposition != layoutPosition)){

            selectedDateposition = layoutPosition;

           // FEApplication.setIsDatePickerClicked(true);
            showProgressDialog();
            dateSelected(layoutPosition);
           // FEApplication.setIsDatePickerClicked(false);
        }

      //  dateList
    }
    private int dpToPx(Context context, int dp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
    private void dateSelected(int position){

        TextView tv_date = (TextView) rv_date_list.getLayoutManager().findViewByPosition(position).findViewById(R.id.tv_date);
        tv_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        HappiApplication.setIsDatePickerClicked(true);
        String selectedDate = dateList.get(position) ;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd");
        Date selected = null;
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        selectedDate = selectedDate +" "+ calendar.get(Calendar.YEAR);
        Date date = null;
        try{
            selected = sdf.parse(selectedDate);
            calendar.setTime(selected);
            date = calendar.getTime();

            Calendar currentCal = Calendar.getInstance();
            Date current = currentCal.getTime();
            //if(!current.after(date) && !current.before(date)){
            if(     (currentCal.get(Calendar.YEAR)== calendar.get(Calendar.YEAR)) &&
                    (currentCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) &&
                    (currentCal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))){
                updateSchedule(scheduleList);
            }else{
                getScheduleForSelectedDate(date);
            }



        }catch(Exception ex){
            Log.e("DATE ERROR"," "+ex.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        onBack();
    }
}
