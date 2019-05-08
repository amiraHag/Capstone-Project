package com.example.android.getfit.ui;


import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.getfit.R;
import com.example.android.getfit.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class SettingsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Initialize Views using butterknife
    @BindView(R.id.bt_recording)
    ToggleButton tbRecordingSubscription;
    @BindView(R.id.bt_update_today)
    Button btUpdateToday;
    @BindView(R.id.bt_update_yesterday)
    Button btUpdateYesterday;
    @BindView(R.id.bt_clear_history)
    Button btClearHistory;

    //initialize variables
    private ResultCallback<Status> mRecordingSubscribeResultCallback;
    private ResultCallback<Status> mRecordingCancelSubscriptionResultCallback;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, Constants.AWAIT_VALUE, this)
                .build();

        initActions();



    }

    private void initActions(){
        mRecordingSubscribeResultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    if (status.getStatusCode()==FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                        Constants.displayToast(getApplicationContext(), getString(R.string.already_subscribed) );
                    } else {
                        Constants.displayToast(getApplicationContext(), getString(R.string.subscribed) );
                    }
                }
            }
        };

        mRecordingCancelSubscriptionResultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Constants.displayToast(getApplicationContext(), getString(R.string.canceled_subscribtion) );
                } else {
                    Constants.displayToast(getApplicationContext(), getString(R.string.canceled_failed) );
                }
            }
        };
        tbRecordingSubscription.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                if (isChecked) {
                    makeSubscriptions();
                } else {
                    // The toggle is disabled
                    cancelSubscriptions();
                }
            }
        });
        btUpdateToday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog(getString(R.string.today));
            }
        });

        btUpdateYesterday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDialog(getString(R.string.yesterday));
            }
        });

        btClearHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DeleteHistoryStepsTask().execute();
            }
        });

    }


    private void cancelSubscriptions() {
        Fitness.RecordingApi.unsubscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(mRecordingCancelSubscriptionResultCallback);
    }

    private void makeSubscriptions() {
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(mRecordingSubscribeResultCallback);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        makeSubscriptions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Constants.displayToast(this, getString(R.string.connection_suspended));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Constants.displayToast(this, getString(R.string.connection_failed));
    }


    public void showDialog(String day) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_steps_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText editTextTitle = dialog.findViewById(R.id.et_weight);
        TextView textViewAdd = dialog.findViewById(R.id.textViewAdd);
        TextView textViewCancel = dialog.findViewById(R.id.textViewCancel);
        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title = editTextTitle.getText().toString();
                Integer stepsValue = Integer.parseInt(Title);
                if(day.equalsIgnoreCase(getString(R.string.today))){
                    new TodayStepsGoogleFitTask(stepsValue).execute();
                }else if(day.equalsIgnoreCase(getString(R.string.yesterday))){
                    new YesterdayStepsGoogleFitTask(stepsValue).execute();
                }


                dialog.dismiss();
            }
        });
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }


    private void addTodayStepsToGoogleFit(int stepsUpdated) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, Constants.TODAY_END_BEFORE);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, Constants.TODAY_START_BEFORE);
        long startTime = cal.getTimeInMillis();
        setStepsOnGoogleFit(startTime, endTime, stepsUpdated);
    }

    private void addYesterdayStepToGoogleFit(int steps) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, Constants.YESTERDAY_END_BEFORE);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, Constants.YESTERDAY_START_BEFORE);
        long startTime = cal.getTimeInMillis();
        setStepsOnGoogleFit(startTime, endTime, steps);

         }
    private void setStepsOnGoogleFit(long startTime, long endTime, int steps){
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName(getString(R.string.steps_add_googlefit))
                .setType(DataSource.TYPE_RAW)
                .build();
        DataSet dataSet = DataSet.create(dataSource);
        DataPoint point = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        point.getValue(Field.FIELD_STEPS).setInt(steps);
        dataSet.add(point);
        DataUpdateRequest updateRequest = new DataUpdateRequest.Builder().setDataSet(dataSet).setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS).build();
        Fitness.HistoryApi.updateData(mGoogleApiClient, updateRequest).await(Constants.AWAIT_VALUE, TimeUnit.MINUTES);


    }

    private void deleteStepsFromGoogleFit() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, Constants.WEEK_END_BEFORE);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, Constants.WEEK_START_BEFORE);
        long startTime = cal.getTimeInMillis();

        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        Fitness.HistoryApi.deleteData(mGoogleApiClient, request).await(Constants.AWAIT_VALUE, TimeUnit.MINUTES);
    }


    private class TodayStepsGoogleFitTask extends AsyncTask<Void, Void, Void> {
        int todayStepsAdded;

        public TodayStepsGoogleFitTask(int steps) {
            super();
            todayStepsAdded = steps;
        }

        protected Void doInBackground(Void... params) {
            addTodayStepsToGoogleFit(todayStepsAdded);
            return null;
        }
    }

    private class YesterdayStepsGoogleFitTask extends AsyncTask<Void, Void, Void> {
        int yesterdayStepsAdded;

        public YesterdayStepsGoogleFitTask(int steps) {
            super();
            this.yesterdayStepsAdded = steps;

        }

        protected Void doInBackground(Void... params) {
            addYesterdayStepToGoogleFit(yesterdayStepsAdded);
            return null;
        }
    }

    private class DeleteHistoryStepsTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            deleteStepsFromGoogleFit();
            return null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         if(item.getItemId() == R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}

