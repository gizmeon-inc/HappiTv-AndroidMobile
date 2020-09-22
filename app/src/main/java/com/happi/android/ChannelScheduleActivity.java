package com.happi.android;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happi.android.adapters.ChannelScheduleAdapter;
import com.happi.android.common.HappiApplication;
import com.happi.android.common.ActivityChooser;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.customviews.TypefacedTextViewRegular;
import com.happi.android.models.ChannelScheduleModel;
import com.happi.android.utils.ConstantUtils;
import com.happi.android.webservice.ApiClient;

import java.text.ParseException;
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

public class ChannelScheduleActivity extends BaseActivity implements View.OnClickListener, ChannelScheduleAdapter.itemClickListener {
    ImageView iv_menu;
    ImageView iv_back;
    ImageView iv_logo_text;
    TypefacedTextViewRegular tv_title;
    LinearLayout rl_end_icons;
    RecyclerView rv_channel_schedule;
    private CompositeDisposable compositeDisposable;
    private int channelId =0;
    private String channel_thumbnail ="";
    ChannelScheduleAdapter channelScheduleAdapter;
    List<ChannelScheduleModel> channelScheduleModel;
    LinearLayout ll_nodata;
    ProgressDialog progressDialog;

    TextView tv_date_view;
    TextView tv_errormsg;
    private String selectedDate = "empty";
    boolean showpgm = false;

    String pattern = "yyyy-MM-dd";
    SimpleDateFormat simpleDateFormat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_schedule);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        if (SharedPreferenceUtility.isNightMode()) {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_black));
        } else {

            this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            int flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            this.getWindow().getDecorView().setSystemUiVisibility(flags);
        }

        iv_menu = findViewById(R.id.iv_menu);
        iv_back = findViewById(R.id.iv_back);
        iv_logo_text = findViewById(R.id.iv_logo_text);
        tv_title = findViewById(R.id.tv_title);
        rl_end_icons = findViewById(R.id.rl_end_icons);
        tv_date_view = findViewById(R.id.tv_date_view);
        rv_channel_schedule = findViewById(R.id.rv_channel_schedule);
        ll_nodata = findViewById(R.id.ll_nodata);
        tv_errormsg = findViewById(R.id.tv_errormsg);

        Intent intent = getIntent();
        channelId = intent.getIntExtra(ConstantUtils.CHANNEL_ID, 0);
        channel_thumbnail = intent.getStringExtra(ConstantUtils.CHANNEL_thumbnail);

        simpleDateFormat = new SimpleDateFormat(pattern,Locale.getDefault());
        //currentDate = simpleDateFormat.format(new Date());

        progressDialog = new ProgressDialog(ChannelScheduleActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);


        channelScheduleAdapter = new ChannelScheduleAdapter(this,this::onScheduleItemClicked);
        channelScheduleModel = new ArrayList<>();
        rv_channel_schedule.setLayoutManager(new LinearLayoutManager(this));
        rv_channel_schedule.setAdapter(channelScheduleAdapter);
        compositeDisposable = new CompositeDisposable();

        getSessionToken();
        progressDialog.show();

        iv_menu.setVisibility(View.GONE);
        rl_end_icons.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        iv_logo_text.setVisibility(View.GONE);
        tv_title.setVisibility(View.VISIBLE);

        tv_title.setText(R.string.programs);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelScheduleActivity.super.onBackPressed();
            }
        });
        tv_date_view.setOnClickListener(this);

    }
    private void getSessionToken() {
        String appKey = SharedPreferenceUtility.getAppKey();
        String bundleId = SharedPreferenceUtility.getBundleID();

        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(),appKey,bundleId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {
                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());                            getChannelSchedule();

                        }, throwable -> {
                            displayErrorLayout(R.string.server_error_please_try_again);

                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void getChannelSchedule()
    {


        String currentDate = simpleDateFormat.format(new Date());

        SimpleDateFormat serverDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat localDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        serverDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        localDate.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat serverDateSet = new SimpleDateFormat("MMM dd, yyyy",Locale.getDefault());
        SimpleDateFormat serverDSet = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        serverDSet.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());

       // Log.e("SELECTED","rawDateTime "+rawDateTime);

         String setSelectedDate = "";
         String startSelectedDate = "";
         String endSelectedDate = "";
         String start = " 00:00:00";
         String end = " 23:59:59";
         Date startofSelectedDate = null;
         Date endofSelectedDate = null;





        try {
                if(selectedDate.equals("empty"))
                {
                    ////////////////////////////////////////////////////////////////
                    startofSelectedDate = localDate.parse(currentDate+start);
                    endofSelectedDate = localDate.parse(currentDate+end);
                    startSelectedDate = serverDSet.format(startofSelectedDate);
                    endSelectedDate = serverDSet.format(endofSelectedDate);

                    setSelectedDate = serverDateSet.format(new Date());
                    tv_date_view.setText(setSelectedDate);
                    ////////////////////////////////////////////////////////////////////

                   // Log.e("SELECTED","filteredDate "+filteredDate);
                   // Log.e("SELECTED","setSelectedDate "+setSelectedDate);

                }
                else{
                    startofSelectedDate = localDate.parse(selectedDate+start);
                    endofSelectedDate = localDate.parse(selectedDate+end);
                    startSelectedDate = serverDSet.format(startofSelectedDate);
                    endSelectedDate = serverDSet.format(endofSelectedDate);

                    setSelectedDate = serverDateSet.format(localDateFormat.parse(selectedDate));
                    tv_date_view.setText(setSelectedDate);

                   // Log.e("SELECTED","filteredDate else "+filteredDate);
                   // Log.e("SELECTED","filteredDate else"+setSelectedDate);


                }
                } catch (ParseException e) {
                e.printStackTrace();
            }
            ApiClient.UsersService usersService = ApiClient.create();
            Disposable videoDisposable = usersService.GetChannelSchedule(HappiApplication.getAppToken(),"android-phone", SharedPreferenceUtility.getCountryCode(),startSelectedDate,endSelectedDate,channelId, SharedPreferenceUtility.getPublisher_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(channelScheduleResponseModel -> {
                    if (channelScheduleResponseModel.getData().size()!=0) {

                        channelScheduleModel = channelScheduleResponseModel.getData();
                        programLiveCheck(channelScheduleModel);
                        channelScheduleAdapter.updateList(channelScheduleResponseModel.getData());
                        progressDialog.dismiss();

                        ll_nodata.setVisibility(View.GONE);
                        rv_channel_schedule.setVisibility(View.VISIBLE);



                    } else {
                        ll_nodata.setVisibility(View.VISIBLE);
                        rv_channel_schedule.setVisibility(View.GONE);
                        progressDialog.dismiss();

                        // Toast.makeText(FEApplication.getCurrentContext(),"NO DATA",Toast.LENGTH_LONG).show();

                    }

                }, throwable -> {
                    displayErrorLayout(R.string.server_error_please_try_again);

                        });
        compositeDisposable.add(videoDisposable);

    }

public void programLiveCheck( List<ChannelScheduleModel> channelScheduleModel){
        showpgm = false;
        if(!channelScheduleModel.isEmpty())
        {
            for (ChannelScheduleModel cModel:channelScheduleModel) {

                cModel.setImage(channel_thumbnail);

                String startTime = cModel.getStart_time();
                String endTime = cModel.getEnd_time();

                Calendar startCalendar = dateStringToCalendar(startTime);
                Calendar endCalendar = dateStringToCalendar(endTime);

                boolean isPgmLive = isLive(startCalendar,endCalendar);

                if(isPgmLive){
                    cModel.setStatus_flag(true);
                }else{
                    cModel.setStatus_flag(false);
                }
                String pgmStartTime = dateToTime12h(startCalendar.getTime());
                String pgmEndTime = dateToTime12h(endCalendar.getTime());

                    cModel.setPgm_start_time(pgmStartTime);
                    cModel.setPgm_end_time(pgmEndTime);

            }
        }else{
            displayErrorLayout(R.string.server_error_please_try_again);
            progressDialog.dismiss();

        }
    }

private Calendar dateStringToCalendar(String dateTime){
    String dateTimeString = dateTime;
    Calendar calendar = Calendar.getInstance(Locale.getDefault());
    SimpleDateFormat utcdateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    utcdateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    SimpleDateFormat localdateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    localdateTimeFormat.setTimeZone(TimeZone.getDefault());
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date dateLocal = null;
    String localdatetime="";
    Date finalDate=null;

    try {
        dateLocal = utcdateTimeFormat.parse(dateTimeString);
        localdatetime = localdateTimeFormat.format(dateLocal);
        finalDate=dateTimeFormat.parse(localdatetime);
    } catch (ParseException e) {
        e.printStackTrace();
    }
    calendar.setTime(finalDate);
        return calendar;
    }
    private boolean isLive(Calendar start,Calendar end){
        boolean live = false;
        Calendar currentCalendarLocal = Calendar.getInstance(Locale.getDefault());

        if(currentCalendarLocal.getTime().after(start.getTime()) && currentCalendarLocal.getTime().before(end.getTime()))
        {
            live = true;
        }else{
            live = false;
        }
        return live;
    }
    private String dateToTime12h(Date date){
        String time12h="";
        SimpleDateFormat timeMeridianFormat = new SimpleDateFormat("hh:mm:ss a");
        time12h = timeMeridianFormat.format(date);

        return time12h;
    }
    private void displayErrorLayout(int errorId) {
        rv_channel_schedule.setVisibility(View.GONE);
        tv_errormsg.setVisibility(View.VISIBLE);
        tv_errormsg.setText(getString(errorId));
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        if(v==tv_date_view) {
            int mYear, mMonth, mDay;
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        monthOfYear = monthOfYear+1;
                        String month = Integer.toString(monthOfYear);
                        String monthSelected="";
                      //  Log.e("SELECTED","monthOfYear"+month);

                        if(month.length()<2)
                        {
                            monthSelected = "0"+month;
                          //  Log.e("SELECTED","newMonth"+monthSelected);

                        }else{
                          //  Log.e("SELECTED","unchangedMonth"+monthSelected);

                            monthSelected = Integer.toString(monthOfYear);
                        }
                        String day = Integer.toString(dayOfMonth);
                        String daySelected="";
                        if(day.length()<2)
                        {
                            daySelected = "0"+day;
                          //  Log.e("SELECTED","newDAy"+daySelected);

                        }else{

                            daySelected = Integer.toString(dayOfMonth);
                         //   Log.e("SELECTED","unchangedDAY"+daySelected);

                        }


                        selectedDate = year+"-"+monthSelected+"-"+daySelected;
                     //   Log.e("SELECTED",""+selectedDate);

                        getChannelSchedule();

                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();


        }


    }

    @Override
    public void onScheduleItemClicked(int adapterPosition) {

        ChannelScheduleModel cModel = channelScheduleModel.get(adapterPosition);


        if(cModel.getStatus_flag())
        {
            ActivityChooser.goToHome(ConstantUtils.CHANNEL_HOME_ACTIVITY,channelId);

        }
        else{
            //Toast.makeText(this,"NOT CLICKABLE",Toast.LENGTH_LONG).show();
        }
    }


}