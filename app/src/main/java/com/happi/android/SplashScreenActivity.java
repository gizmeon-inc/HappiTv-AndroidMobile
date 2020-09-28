package com.happi.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.happi.android.common.AdvertisingIdAsyncTask;
import com.happi.android.common.HappiApplication;
import com.google.gson.JsonObject;
import com.happi.android.common.BaseActivity;
import com.happi.android.common.LocationTrack;
import com.happi.android.common.SharedPreferenceUtility;
import com.happi.android.utils.AppUtils;
import com.happi.android.webservice.ApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SplashScreenActivity extends BaseActivity implements LocationTrack.gpsToggleInterface {

    //=================================================
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private final static int LOCATIONS = 110;
    LocationTrack locationTrack;
    //===============================================

   // private String appKey="111";
   // private String bundleId="999";

     private String appKey="happifilms";
     private String bundleId="com.happifilms.androidtest";

    CompositeDisposable compositeDisposable;
    String showId = "empty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash_screen);

        //uncomment for analytics
        HappiApplication.setApplaunch(true);
        HappiApplication.setCurrentContext(this);
        if (SharedPreferenceUtility.getAdvertisingId().isEmpty()) {
            new AdvertisingIdAsyncTask().execute();
        }
        Intent newIntent = getIntent();
        Uri data = newIntent.getData();
        String action = newIntent.getAction();

        if ( data != null) {
            showId = data.getQueryParameter("show");
        }


        HappiApplication.setCurrentContext(this);
        compositeDisposable = new CompositeDisposable();

        setCredentials();
        //getSessionToken();

    }

    private void checkLocationPermission(){
        if(SharedPreferenceUtility.isLocationAccepted() == 2){
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getApplicationContext(),
                            ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Permission not Granted...
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION},
                            LOCATIONS);
                }

            } else {

                //Permission has been already Granted...

            }

        }
    }

    private void getPubID() {
        ApiClient.UsersService usersService = ApiClient.create();
        Disposable pubidDisposable = usersService.getPubID( appKey,bundleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(publisherModel ->{
                    if(publisherModel.getPubid()!=null){
                        Log.e("pubid",publisherModel.getPubid());
                        SharedPreferenceUtility.setPublisher_id(publisherModel.getPubid());
                      /*  if (SharedPreferenceUtility.getUserId() == 0) {
                            goToLoginPage();
                        }else{
                            goToHomePage();
                        }*/
                      check();

                    }else{
                        Toast.makeText(this,"Server error",Toast.LENGTH_SHORT).show();
                    }

                    }, throwable -> {
                    Toast.makeText(this,"Server error",Toast.LENGTH_SHORT).show();

                });
        compositeDisposable.add(pubidDisposable);
    }

    private void setCredentials()
    {
        SharedPreferenceUtility.setAppkey(appKey);
        SharedPreferenceUtility.setBundleId(bundleId);
    }
    private void goToLoginPage() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        intent.putExtra("show",showId);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void goToHomePage() {
        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        intent.putExtra("show",showId);
        HappiApplication.setIsFromLink(true);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onStart() {

        super.onStart();
       // check();
        getPubID();
    }

    private void check(){
        //======================================================
        if (SharedPreferenceUtility.isLocationAccepted() == 0) {

            permissions.add(ACCESS_FINE_LOCATION);
            permissions.add(ACCESS_COARSE_LOCATION);
            permissionsToRequest = findUnAskedPermissions(permissions);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionsToRequest.size() > 0) {

                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            } else {

                fetchLocationDetails();
            }
        } else {

            fetchLocationDetails();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (AppUtils.isDeviceRooted()) {
            showAlertDialogAndExitApp("This device is rooted. You can't use this app.");
        }
    }


    private boolean checkTrial(){
        boolean val = false;

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            String dateEXP = "10/04/2019";
            String dateTODAY = formatter.format(c);

            Date expDate = formatter.parse(dateEXP);
            Date todayDate= formatter.parse(dateTODAY);
            if (todayDate.after(expDate)) {
                val = true;
            }

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return val;
    }

    //================================================

    private void checkAndNav() {

        if (SharedPreferenceUtility.getGuestStatus()) {

            getSessionToken();

        } else {
            if (SharedPreferenceUtility.getUserId() == 0) {

               final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    goToLoginPage();
                }, 500);
            } else {

                final Handler handler = new Handler();
               handler.postDelayed(() -> {

                  //  goToHomePage();
                   getSessionToken();
                }, 500);
            }
        }
    }

    private void navigate() {


        if (SharedPreferenceUtility.getUserId() == 0) {

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                goToLoginPage();
            }, 500);
        } else {

            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                 goToHomePage();
            }, 500);
        }
      // getPubID();
    }

    private void getSessionToken() {


        ApiClient.TokenService tokenService = ApiClient.token();
        Disposable tokenDisposable =
                tokenService.getSessionToken(SharedPreferenceUtility.getUserId(),appKey,bundleId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sessionTokenResponseModel -> {


                            HappiApplication.setAppToken(sessionTokenResponseModel.getToken());
                            SharedPreferenceUtility.setApp_Id(sessionTokenResponseModel.getApplication_id());

                      //      HappiApplication.setPublisher_id(sessionTokenResponseModel.getPublisher_id());

                            //Test AdIDs
                            // SharedPreferenceUtility.setAdMobPubIds("ca-app-pub-3940256099942544/6300978111", "ca-app-pub-3940256099942544/5224354917","ca-app-pub-3940256099942544/1033173712","ca-app-pub-3940256099942544~3347511713", "1", "1","24534e1901884e398f1253216226017e","b195f8dd8ded45fe847ad89ed1d016da","1");
                            //Live AdIDs
                            //SharedPreferenceUtility.setAdMobPubIds("ca-app-pub-1674238972502360/1600095046", "ca-app-pub-1674238972502360/8688247573","ca-app-pub-1674238972502360/4202207657");
                            // SharedPreferenceUtility.setAdMobPubIds(sessionTokenResponseModel.getBanner_id(), sessionTokenResponseModel.getRewarded_id(),sessionTokenResponseModel.getInterstitial_id(),sessionTokenResponseModel.getApp_id(),sessionTokenResponseModel.getRewarded_status(),sessionTokenResponseModel.getInterstitial_status(),sessionTokenResponseModel.getMopub_interstitial_id(),sessionTokenResponseModel.getMopub_banner_id(),"0");

                            loginRemovalApiCall();
                        }, throwable -> {

                            Log.e("getSessionToken", throwable.getLocalizedMessage());
                        });
        compositeDisposable.add(tokenDisposable);
    }

    private void loginRemovalApiCall() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", "" + SharedPreferenceUtility.getUserId());
        jsonObject.addProperty("fcm_token", SharedPreferenceUtility.getFcmToken());

        ApiClient.UsersService usersService = ApiClient.create();
        Disposable disposable = usersService.LoginRemoval(HappiApplication.getAppToken(),
                jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponseModel -> {

                    if (loginResponseModel.getStatus() == 1) {

                        if (loginResponseModel.getData().get(0).isValidity()) {

                            navigate();
                        } else {

                            SharedPreferenceUtility.saveUserDetails(0, "", "", "", "", "", "",
                                    "", false,"");
                            goToLoginPage();
                        }
                    } else if (loginResponseModel.getStatus() == 2) {

                        navigate();
                    }
                }, throwable -> {

                    Toast.makeText(this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                });
        compositeDisposable.add(disposable);
    }

    //================================================

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {

                    SharedPreferenceUtility.setLocationAcceptance(2);
                    ipAddressApiCall();
                 //   navigate();
                } else {

                    fetchLocationDetails();
                }
                break;

        }
    }

    @Override
    public void onGPStoggled(boolean gpsStatus) {

        if (gpsStatus) {

            SharedPreferenceUtility.setLocationAcceptance(1);
            fetchLocationDetails();
        } else {

            SharedPreferenceUtility.setLocationAcceptance(2);
            ipAddressApiCall();
        }
    }

    private void fetchLocationDetails() {

        locationTrack = new LocationTrack(this, this::onGPStoggled);
        if (!locationTrack.canGetLocation()) {

            //locationTrack.showSettingsAlert();
            ipAddressApiCall();
        } else {

            if (locationTrack.getLatitude() != 0.0 && locationTrack.getLatitude() != 0.0) {
                HappiApplication.setLongitude(locationTrack.getLongitude());
                HappiApplication.setLatitude(locationTrack.getLatitude());

                try {
                    Geocoder gcd = new Geocoder(HappiApplication.getCurrentContext(), Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(locationTrack.getLatitude(), locationTrack.getLongitude(), 1);
                    if (addresses.size() > 0) {

                        SharedPreferenceUtility.setCountryCode(addresses.get(0).getCountryCode());
                       // SharedPreferenceUtility.setCity(addresses.get(0));
                        //HappiApplication.setCountryCode("US");
                        if(addresses.get(0).getLocality() != null){
                            HappiApplication.setCity(addresses.get(0).getLocality());
                        }else if(addresses.get(0).getSubAdminArea() != null){
                            HappiApplication.setCity(addresses.get(0).getSubAdminArea());
                        }else if(addresses.get(0).getAdminArea() != null){
                            HappiApplication.setCity(addresses.get(0).getAdminArea());
                        }else{
                            HappiApplication.setCity(addresses.get(0).getCountryName());
                        }
                       // HappiApplication.setCity(addresses.get(0).getLocality());
                        HappiApplication.setRegion(addresses.get(0).getAdminArea());
                        HappiApplication.setCountry(addresses.get(0).getCountryName());
                    }
                    checkAndNav();
                } catch (Exception e) {

                    e.printStackTrace();
                    ipAddressApiCall();
                }
            } else {

                ipAddressApiCall();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationTrack != null) {
            locationTrack.stopListener();
        }
        safelyDispose(compositeDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }

    private void ipAddressApiCall() {

        ApiClient.IpAddressApiService ipAddressApiService = ApiClient.createIPService();
        Disposable ipDisposable = ipAddressApiService.fetchIPAddress()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ipAddressModel -> {

                    SharedPreferenceUtility.setCountryCode(ipAddressModel.getCountryCode());
                    //HappiApplication.setCountryCode("US");
                    HappiApplication.setCity(ipAddressModel.getCity());
                    HappiApplication.setLatitude(ipAddressModel.getLat());
                    HappiApplication.setLongitude(ipAddressModel.getLon());
                    HappiApplication.setRegion(ipAddressModel.getRegion());
                    HappiApplication.setIpAddress(ipAddressModel.getQuery());
                    HappiApplication.setCountry(ipAddressModel.getCountry());

                    checkAndNav();
                }, throwable -> {

                    checkAndNav();
                });
        compositeDisposable.add(ipDisposable);
    }

    //================================================
}