package com.happi.android.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.view.Display;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.happi.android.common.HappiApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.happi.android.common.HappiApplication.getCurrentActivity;

public class AppUtils {

    //Check if device is rooted
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() ||
                checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su",
                "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
                "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{
                    "/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        return new Point(width, height);
    }

    //Exit App
    public static void exitApp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HappiApplication.getCurrentContext());
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        exitPoppoTv();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static void exitPoppoTv() {
        callAnalyticsEventApi();
        getCurrentActivity().finish();
        Intent exitIntent = new Intent(Intent.ACTION_MAIN);
        exitIntent.addCategory(Intent.CATEGORY_HOME);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getCurrentActivity().startActivity(exitIntent);
    }

    public static boolean isOnline() {

        ConnectivityManager connectivity = (ConnectivityManager) HappiApplication
                .getCurrentContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);


        return (connectivity != null
                && connectivity.getActiveNetworkInfo() != null && connectivity
                .getActiveNetworkInfo().isConnected());

    }

    //analytics
    private static void callAnalyticsEventApi() {

        //Uncomment to enable analytics api call

       /* Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long epoch = currentCalendar.getTimeInMillis() / 1000L;
        String device_id = Settings.Secure.getString(HappiApplication.getCurrentContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        JsonObject eventDetails = new JsonObject();
        eventDetails.addProperty("device_id",device_id);
        eventDetails.addProperty("user_id", String.valueOf(SharedPreferenceUtility.getUserId()));
        eventDetails.addProperty("event_type","POP07");
        eventDetails.addProperty("timestamp",String.valueOf(epoch));
        eventDetails.addProperty("app_id",SharedPreferenceUtility.getappId());

        try {
            Log.e("000##", "POP07: " + "api about to call");
            AnalyticsApi.AnalyticsServiceScalar analyticsServiceScalar = AnalyticsApi.createScalar();
            Call<String> call = analyticsServiceScalar.eventCall2(eventDetails);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.e("000##", "success: " +"POP07"+response.code());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("000##", "failure: " +"POP07"+t.toString());
                }
            });
        }catch(Exception ex){
            Log.e("000##", "Exception: " +"POP07"+ex.toString());
        }*/

    }

    public static String getDay(Date finalStartDateTime) {
        String status = "";
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();
        int value;
        if (currentDate.before(finalStartDateTime)) {
            value = 1;
        } else if (currentDate.after(finalStartDateTime)) {
            value = -1;
        } else {
            value = 0;
        }

        if (value == 0) {
            status = "Today";
        } else if (value == 1) {
            int difference = (int) getDateDiff(currentDate, finalStartDateTime, TimeUnit.DAYS);
            if (difference == 0) {
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(finalStartDateTime);

                if (startCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR) &&
                        startCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
                    status = "Today";
                } else {
                    status = "Tomorrow";
                }
            } else if (difference == 1) {
                SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String startDateString = sdfLocal.format(finalStartDateTime);
                boolean isTomorrow = checkTomorrow(startDateString);
                if (isTomorrow) {
                    status = "Tomorrow";
                } else {
                    String weekday = "";
                    SimpleDateFormat sdfLocalDay = new SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault());

                    String dayStart = sdfLocalDay.format(finalStartDateTime);
                    String[] date = dayStart.split(" ");
                    if (date.length > 1) {
                        weekday = date[1];
                    } else {
                        weekday = "";
                    }

                    status = weekday;
                }

            } else {
                status = "";
            }

        } else {
            status = "";
        }


        return status;
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {

        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private static boolean checkTomorrow(String date) {

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 1);
        String currentDate = sdfDate.format(today.getTime());
        return currentDate.equals(date);
    }

}
