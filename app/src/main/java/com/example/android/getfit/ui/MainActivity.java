package com.example.android.getfit.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.android.getfit.Helpers.BackgroundTrackingActivityService;
import com.example.android.getfit.R;
import com.example.android.getfit.db.Bmi;
import com.example.android.getfit.utils.Constants;
import com.example.android.getfit.viewmodel.BmiViewModel;
import com.example.android.getfit.widget.MyAppWidgetService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.DetectedActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.img_activity_bmi)
    ImageView ivBMIButton;
    @BindView(R.id.img_activity_tracking)
    ImageView ivActivityTrackingButton;
    @BindView(R.id.img_activity_steps)
    ImageView ivStepsButton;
    @BindView(R.id.txt_activity)
    TextView txtActivity;
    @BindView(R.id.txt_confidence)
    TextView txtConfidence;
    @BindView(R.id.tv_last_bmi)
    TextView mLastNoteTextView;
    @BindView(R.id.img_activity)
    ImageView imgActivity;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar myToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout ctl;

    private BroadcastReceiver broadcastReceiver;
    private AdView mAdView;
    private BmiViewModel mBmiViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
        ctl.setTitle(getString(R.string.app_name));

        initActions();
        startTracking();
        setLatestBmi();
        initAdmob();

    }

    private void initActions() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(getString(R.string.broadcast_intent_activity_detected))) {
                    int type = intent.getIntExtra(getString(R.string.activity_type), Constants.DEFAULT_ACTIVITY_TYPE);
                    int confidence = intent.getIntExtra(getString(R.string.activity_confidence), Constants.DEFAULT_ACTIVITY_CONFIDENCE);
                    String confidences = intent.getStringExtra(getString(R.string.broadcast_confidences));
                    trackUserActivity(type, confidence);
                }
            }
        };
        ivBMIButton.setOnClickListener(new View.OnClickListener() {

                                           @Override
                                           public void onClick(View view) {
                                               Intent intent = new Intent(view.getContext(), BMIActivity.class);
                                               startActivity(intent);

                                           }
                                       }
        );
        ivActivityTrackingButton.setOnClickListener(new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent = new Intent(view.getContext(), TrackingActivity.class);

                                                            startActivity(intent);

                                                        }
                                                    }
        );
        ivStepsButton.setOnClickListener(new View.OnClickListener() {

                                             @Override
                                             public void onClick(View view) {
                                                 Intent intent = new Intent(view.getContext(), StepsActivity.class);
                                                 startActivity(intent);
                                             }
                                         }
        );
    }

    private void initAdmob() {

        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.device_admob_test))
                .build();
        mAdView.loadAd(adRequest);
    }

    private void trackUserActivity(int state, int confidence) {
        String activityLabel = getString(Constants.identifyActvityTitle(state));
        int activityIcon = Constants.identifyActvityIcon(state);

        if (confidence > Constants.CONFIDENCE) {
            txtActivity.setText(activityLabel);
            txtConfidence.setText(getString(R.string.activity_confidence_label_text) + confidence);
            imgActivity.setImageResource(activityIcon);
        }


    }

    private void setLatestBmi() {
        Paper.init(this);
        mBmiViewModel = ViewModelProviders.of(this).get(BmiViewModel.class);
        mBmiViewModel.getLastBmi().observe(this, new Observer<Bmi>() {
            @Override
            public void onChanged(@Nullable Bmi bmi) {
                if (bmi!=null) {
                    mLastNoteTextView.setText(getString(R.string.latest_bmi_value_label) + bmi.getBmiValue());
                    MyAppWidgetService.initWidgetContent(getBaseContext(), bmi, true);

                } else {
                    mLastNoteTextView.setText(" ");


                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(getString(R.string.broadcast_intent_activity_detected)));
        startTracking();
        if (mAdView!=null) {
            mAdView.resume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        stopTracking();
        if (mAdView!=null) {
            mAdView.pause();
        }

    }

    @Override
    public void onDestroy() {
        if (mAdView!=null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void startTracking() {
        Intent intent1 = new Intent(MainActivity.this, BackgroundTrackingActivityService.class);
        startService(intent1);
    }

    private void stopTracking() {
        Intent intent = new Intent(MainActivity.this, BackgroundTrackingActivityService.class);
        stopService(intent);
    }
}