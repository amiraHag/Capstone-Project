package com.example.android.getfit.Helpers;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.getfit.R;
import com.example.android.getfit.utils.Constants;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class BackgroundTrackingActivityService extends Service {


    IBinder mIBinder = new BackgroundTrackingActivityService.LocalBinder();
    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    public BackgroundTrackingActivityService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntentService = new Intent(this, ActivityTrackingIntentService.class);
        mPendingIntent = PendingIntent.getService(this, Constants.REQUEST_CODE_OAUTH, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestUpdateTrackingHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void requestUpdateTrackingHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Constants.DETECTION_INTERVAL_MILLISECONDS,
                mPendingIntent);
        addTaskButtonAction(task, getString(R.string.request));
    }

    public void removeUpdateTrackingHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        addTaskButtonAction(task, getString(R.string.remove));
    }

    public void addTaskButtonAction(Task<Void> task, String request) {
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {

                Constants.displayToast(getApplicationContext(), request + " "+ getString(R.string.tracking_success));
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Constants.displayToast(getApplicationContext(), request +" "+ getString(R.string.tracking_fail));
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeUpdateTrackingHandler();
    }

    public class LocalBinder extends Binder {
        public BackgroundTrackingActivityService getServerInstance() {
            return BackgroundTrackingActivityService.this;
        }
    }
}
