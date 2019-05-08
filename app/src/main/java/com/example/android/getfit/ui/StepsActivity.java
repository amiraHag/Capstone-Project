package com.example.android.getfit.ui;

import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.getfit.R;
import com.example.android.getfit.utils.Constants;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.txusballesteros.widgets.FitChart;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.content.IntentSender;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.List;

public class StepsActivity extends AppCompatActivity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    //Initialize Views using butterknife
    @BindView(R.id.tv_steps_today_chart)
    TextView mStepsChartTextView;
    @BindView(R.id.tv_week_history)
    TextView mTextViewWeekHistory;
    @BindView(R.id.chart_steps_today)
    FitChart mFitChart;

    //initialize variables
    private OnDataPointListener listener;
    private boolean isAuthInProgress = false;
    private GoogleApiClient mGoogleApiClient;
    private DataSourcesRequest dataSourceRequest;
    private ResultCallback<DataSourcesResult> dataSourcesResultCallback;
    private SensorRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);
        ButterKnife.bind(this);

        if (savedInstanceState!=null) {
            isAuthInProgress = savedInstanceState.getBoolean(getString(R.string.auth_state_pending));
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, 0, this)
                .build();

        displayViews();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(getString(R.string.auth_state_pending), isAuthInProgress);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        displayStepsValues();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        displayStepsValues();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectSensorApi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectSensorApi();
    }


    @Override
    public void onConnected(Bundle bundle) {
        dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build();

        dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                    if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(dataSource.getDataType())) {
                        addFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mGoogleApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!isAuthInProgress) {
            try {
                isAuthInProgress = true;
                connectionResult.startResolutionForResult(StepsActivity.this, Constants.REQUEST_CODE_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Constants.displayToast(this, getString(R.string.auth_failed));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==Constants.REQUEST_CODE_OAUTH) {
            isAuthInProgress = false;
            if (resultCode==RESULT_OK) {
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode==RESULT_CANCELED) {
                Constants.displayToast(this, getString(R.string.auth_cancelled));
            }
        } else {
            Constants.displayToast(this, getString(R.string.auth_failed));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Constants.displayToast(this, getString(R.string.connection_suspended));
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for (final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue(field);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTodayStepViews(value.asInt());

                }
            });

        }
    }


    public void displayViews() {
        mFitChart.setMinValue(Constants.STEP_CHART_START_VALUE);
        mFitChart.setMaxValue(Constants.STEP_CHART_FINAL_VALUE);
        displayStepsValues();
    }

    public void displayStepsValues() {
        new ViewWeekStepHistoryTask().execute();
    }


    public void setTodayStepViews(int value){

        mStepsChartTextView.setText(getString(R.string.steps_title) + value);
        mFitChart.setValue(value);
        Constants.displayToast(getApplicationContext(), getString(R.string.steps_updated));
    }


    private void disconnectSensorApi() {
        Fitness.SensorsApi.remove(mGoogleApiClient, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mGoogleApiClient.disconnect();
                        }
                    }
                });
    }

    private void addFitnessDataListener(DataSource dataSource, DataType dataType) {

        request = new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(Constants.SAMPLING_RATE, TimeUnit.SECONDS)
                .build();

        listener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (final Field field : dataPoint.getDataType().getFields()) {
                    final Value value = dataPoint.getValue(field);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTodayStepViews(value.asInt());

                        }
                    });
                }

            }
        };

        Fitness.SensorsApi.add(mGoogleApiClient, request, listener);
    }

  /*  private void displayTodayTotalStepsData() {
        dataReadResult = Fitness.HistoryApi
                .readDailyTotal(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .await(Constants.AWAIT_VALUE, TimeUnit.MINUTES);

        showStepsDataSet(dataReadResult.getTotal());
    }
*/

    private void displayLastWeeksData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.add(Calendar.DAY_OF_YEAR, Constants.WEEK_END_BEFORE);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, Constants.WEEK_START_BEFORE);
        long startTime = cal.getTimeInMillis();


        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName(getString(R.string.week_steps_stream_name))
                .setAppPackageName(getString(R.string.week_steps_pakage_name))
                .build();
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(Constants.AWAIT_VALUE, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();


        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(Constants.AWAIT_VALUE, TimeUnit.MINUTES);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewWeekHistory.setText(" ");
            }
        });

        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        } else {
              if (dataReadResult.getDataSets().size() > 0) {
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    showDataSet(dataSet);
                }
            }
        }
    }

  /*  private void showStepsDataSet(DataSet dataSet) {

        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stepsTodayHistory.setText(dp.getValue(field) + "\n\n");
                    }
                });
            }
        }
    }*/


    private void showDataSet(DataSet dataSet) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextViewWeekHistory.setText(mTextViewWeekHistory.getText() +
                                "\t" +  getString(R.string.start)+": " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))
                                +"\t" + getString(R.string.end)+": " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))
                                + "\n \t" + field.getName() +
                                ": " + dp.getValue(field) + "\n\n");

                        setTodayStepViews(dp.getValue(field).asInt());

                    }
                });

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stpes_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.steps_Settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private class ViewWeekStepHistoryTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData();
            return null;
        }
    }


}