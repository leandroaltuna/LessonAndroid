package com.example.fifthlesson.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;


public class MejorandroidApplication extends Application {

    private static final String TAG = MejorandroidApplication.class.getSimpleName();

    private boolean serviceRunningFlag;

    public boolean isServiceRunningFlag()
    {
        return serviceRunningFlag;
    }

    public void setServiceRunningFlag(boolean serviceRunningFlag)
    {
        this.serviceRunningFlag = serviceRunningFlag;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreated");
        startService(new Intent(this, UpdaterService.class));

    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        Log.d(TAG, "onTerminated");
        stopService(new Intent(this, UpdaterService.class));
    }
}
