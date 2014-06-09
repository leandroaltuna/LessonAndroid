package com.example.fifthlesson.app;


import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.fifthlesson.app.db.DBHelper;
import com.example.fifthlesson.app.db.DBOperations;
import com.example.fifthlesson.app.models.Tweet;
import com.example.fifthlesson.app.utils.ConstantsUtils;
import com.example.fifthlesson.app.utils.TwitterUtils;

import java.util.ArrayList;

public class UpdaterService extends Service {

    private static final String TAG = UpdaterService.class.getSimpleName();

    static final int DELAY = 60000;
    private boolean runFlag = false;

    private Updater updater;
    private MejorandroidApplication application;
    private DBOperations dbOperations;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreated");

        updater = new Updater();
        application = (MejorandroidApplication) getApplication();
        dbOperations = new DBOperations(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroyed");

        runFlag = false;
        application.setServiceRunningFlag(false);
        updater.interrupt();
        updater = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d(TAG, "onStarted");

        runFlag = true;
        application.setServiceRunningFlag(true);
        updater.start();

        return START_STICKY;
    }

    private class Updater extends Thread
    {
        ArrayList<Tweet> timeline = new ArrayList<Tweet>();

        public Updater()
        {
            super("UpdaterService-UpdaterThread");
        }

        @Override
        public void run() {
            UpdaterService updaterService = UpdaterService.this;

            while ( updaterService.runFlag )
            {
                Log.d(TAG, "UpdaterThread running");

                try
                {
                    timeline = TwitterUtils.getTimelineForSearchTerm(ConstantsUtils.MEJORANDROID_TERM);

                    ContentValues values = new ContentValues();

                    for (Tweet tweet : timeline)
                    {
                        values.clear();

                        values.put(DBHelper.C_ID, tweet.getId());
                        values.put(DBHelper.C_NAME, tweet.getName());
                        values.put(DBHelper.C_SCREEN_NAME, tweet.getScreenName());
                        values.put(DBHelper.C_IMAGE_PROFILE_URL, tweet.getProfileImageUrl());
                        values.put(DBHelper.C_TEXT, tweet.getText());
                        values.put(DBHelper.C_CREATED_AT, tweet.getCreatedAt());

                        dbOperations.insertOrIgnore(values);
                    }

                    Thread.sleep(DELAY);

                }
                catch (InterruptedException e)
                {
                    updaterService.runFlag = false;
                    application.setServiceRunningFlag(false);
                }
            }
        }
    }

}
