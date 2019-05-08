package com.example.android.getfit.Helpers;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.android.getfit.R;
import com.example.android.getfit.utils.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivityTrackingIntentService extends IntentService {


    public ActivityTrackingIntentService() {

        super(ActivityTrackingIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        String allActivitiesConfidence
                = " ";
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
        for (DetectedActivity activity : detectedActivities) {
            allActivitiesConfidence += "\n"   + getString(R.string.activity_state_label_text)
                    + " " + getString(Constants.identifyActvityTitle(activity.getType())) +" "
                    + getString(R.string.activity_confidence_label_text) + activity.getConfidence();
            startBroadcastActivity(activity, allActivitiesConfidence);
        }
    }

    private void startBroadcastActivity(DetectedActivity activity, String allActvitiesConfidence) {
        Intent intent = new Intent(getString(R.string.broadcast_intent_activity_detected));
        intent.putExtra(getString(R.string.activity_type), activity.getType());
        intent.putExtra(getString(R.string.activity_confidence), activity.getConfidence());
        intent.putExtra(getString(R.string.broadcast_confidences), allActvitiesConfidence);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
