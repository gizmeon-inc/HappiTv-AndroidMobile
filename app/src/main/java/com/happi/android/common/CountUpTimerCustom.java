package com.happi.android.common;

import android.os.CountDownTimer;

public abstract class CountUpTimerCustom extends CountDownTimer {

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */


    private static final long INTERVAL = 1000;
    private final long duration;
    private long currentTime = 0L;
    private boolean isTimerPaused = false;
    private boolean isTimerCancelled = false;
    private final long durationStatic;
    private long timeLeft = 0L;

    protected CountUpTimerCustom(long durationms) {
        super(durationms, INTERVAL);
        duration = durationms;
        durationStatic = durationms;
        isTimerPaused = true;
    }

    public abstract void onTick(int seconds);

    @Override
    public void onTick(long millisUntilFinished) {
        int milli = (int) (millisUntilFinished/1000);

        if(isTimerCancelled){
           // isTimerCancelled = false;
            int second = (int) (duration/1000);
            onTick(second);
        }else{
          //  timeLeft = millisUntilFinished;
            int second = (int) ((duration - millisUntilFinished)/1000);
            onTick(second);
        }

    }

    @Override
    public void onFinish() {
        isTimerCancelled = true;
        onTick(duration/1000);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public void pauseTimer(long current){
      /*  isTimerPaused = true;
        isTimerCancelled = true;
        currentTime = current;
        onFinish();*/
        isTimerPaused = true;
        isTimerCancelled = true;
        onFinish();
    }
    public void pauseTimer(){
        isTimerPaused = true;
        isTimerCancelled = true;
        onFinish();
    }
    public void resumeTimer(long time){
    if(isTimerPaused){
        isTimerCancelled = false;
        isTimerPaused = false;
        onTick(time);

    }else{
        String s = "bdmbas";
    }





      /*
        if(isTimerPaused){
            isTimerPaused = false;
            if(currentTime != duration && currentTime != (duration-1)){
                int second = (int) ((duration - currentTime)/1000);
                onTick(second);
            }
        }else{

       *//* isTimerPaused = false;
        if(currentTime != duration){
            int second = (int) ((duration - currentTime)/1000);
            onTick(second);
        }else{
//            int second = (int) ((duration - currentTime)/1000);
//            onTick(second);*//*
        }*/

    }


}
