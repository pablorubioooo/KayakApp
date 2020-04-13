package com.example.pablo.kayakapp.training;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;


/**
 * Created by Pablo on 14/03/2018.
 */

public class Background implements Runnable{

    public static final long MIL_TO_MIN = 60000;
    public static final long MIL_TO_HOR = 3600000;
    private Context mContext;
    private long mStartTime;
    public boolean mIsRunning = false;
    private static long since, mPauseTime, mResumeTime, mTransTime;
    private final Object GUI_INITIALIZATION_MONITOR = new Object();
    private boolean pauseThreadFlag = false;


    public Background(Context context){
        mContext = context;
    }

    public void start(){

        mStartTime= System.currentTimeMillis();
        mIsRunning=true;

    }

    public void stop(){
        mIsRunning=false;
    }

    @Override
    public void run(){

        while(mIsRunning){
            checkForPaused();
            since = System.currentTimeMillis() - mStartTime;

            int seconds = (int)((since / 1000) %60);
            int minutes = (int) (((since / MIL_TO_MIN)) % 60);
            int hours = (int) (((since / MIL_TO_HOR)) % 24);
            int milis = (int) since % 1000;

            ((Training) mContext).updateCrono(seconds,minutes,hours,milis);


            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void checkForPaused() {
        synchronized (GUI_INITIALIZATION_MONITOR) {
            while (pauseThreadFlag) {
                try {
                    GUI_INITIALIZATION_MONITOR.wait();
                } catch (Exception e) {}
            }
        }
    }

    public void pauseThread() throws InterruptedException {
        pauseThreadFlag = true;
        mPauseTime = System.currentTimeMillis();
    }

    public void resumeThread() {
        synchronized(GUI_INITIALIZATION_MONITOR) {
            pauseThreadFlag = false;
            GUI_INITIALIZATION_MONITOR.notify();
            mResumeTime = System.currentTimeMillis();
            mTransTime = mResumeTime - mPauseTime;
            mStartTime += mTransTime;
        }
    }

}
