package com.example.android.getfit.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.getfit.Helpers.BackgroundTrackingActivityService;
import com.example.android.getfit.R;
import com.example.android.getfit.utils.Constants;
import com.google.android.gms.location.DetectedActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackingActivity extends AppCompatActivity {



    //initialize variables
    public BroadcastReceiver broadcastReceiver;
    //Initialize Views using butterknife
    @BindView(R.id.all_activities_confidence)
    TextView mTextViewAllActivitiesConfidence;
    @BindView(R.id.tb_tracking_process)
    ToggleButton mButtonViewTrackingProcess;
    @BindView(R.id.tv_tracking_activity)
    TextView mTextViewActivity;
    @BindView(R.id.tv_tracking_confidence)
    TextView mTextViewConfidence;
    @BindView(R.id.iv_tracking_activity)
    ImageView mImageViewActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        ButterKnife.bind(this);
        initActions();
        startTracking();
    }

    private void initActions() {
        mButtonViewTrackingProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                if (isChecked) {
                    startTracking();
                } else {
                    stopTracking();
                }
            }
        });


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(getString(R.string.broadcast_intent_activity_detected))) {
                    int type = intent.getIntExtra(getString(R.string.activity_type), Constants.DEFAULT_ACTIVITY_TYPE);
                    int confidence = intent.getIntExtra(getString(R.string.activity_confidence), Constants.DEFAULT_ACTIVITY_CONFIDENCE);
                    String confidences = intent.getStringExtra(getString(R.string.broadcast_confidences));
                    trackUserActivity(type, confidence, confidences);
                }
            }
        };


    }

    private void trackUserActivity(int state, int confidence, String allConfidences) {
        String activityLabel = getString(Constants.identifyActvityTitle(state));
        int activityIcon = Constants.identifyActvityIcon(state);

        if (confidence > Constants.CONFIDENCE) {
            mTextViewActivity.setText( activityLabel);
            mTextViewConfidence.setText(getString(R.string.activity_confidence_label_text) + confidence);
            mImageViewActivity.setImageResource(activityIcon);

            if (activityLabel.equals(getString(R.string.activity_running))) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.activity_running_notification_channel));
                notificationBuilder.setContentText(getString(R.string.activity_running_notification_text));
                notificationBuilder.setSmallIcon(activityIcon);
                notificationBuilder.setContentTitle(getString(R.string.app_name));
                notificationBuilder.setAutoCancel(true);
                PendingIntent contentIntent = PendingIntent.getActivity(this, Constants.NOTIFICATION_CODE,
                        new Intent(this, TrackingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(contentIntent);
                // Gets an instance of the NotificationManager service
                NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(Constants.NOTIFICATION_CODE, notificationBuilder.build());
            }
        }
        mTextViewAllActivitiesConfidence.setText(allConfidences);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextViewAllActivitiesConfidence.setText("");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(getString(R.string.broadcast_intent_activity_detected)));
        startTracking();

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        stopTracking();

    }

    public void startTracking() {
        Intent intent1 = new Intent(getApplicationContext(), BackgroundTrackingActivityService.class);
        startService(intent1);

    }

    public void stopTracking() {
        Intent intent = new Intent(getApplicationContext(), BackgroundTrackingActivityService.class);
        stopService(intent);
    }
}
