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
    private static void callAnalyticsEventApi(){

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
}
