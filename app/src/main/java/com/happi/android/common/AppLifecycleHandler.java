package com.happi.android.common;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Bundle;

public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    private LifeCycleDelegate lifeCycleDelegate;
    private int activityStarted = 0;

    public AppLifecycleHandler(LifeCycleDelegate lifeCycleDelegate){
        this.lifeCycleDelegate = lifeCycleDelegate;
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        //app in foreground
        activityStarted++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

        //app in background
        activityStarted--;
        if(activityStarted == 0){
            lifeCycleDelegate.onAppBackgrounded();
        }

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onTrimMemory(int level) {

       /* if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN){
            lifeCycleDelegate.onAppBackgrounded();
        }*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }
    public interface LifeCycleDelegate{
        public void onAppBackgrounded();
       // public void onAppForegrounded();
    }
}
