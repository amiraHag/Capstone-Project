package com.example.android.getfit.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.Toast;

import com.example.android.getfit.R;
import com.google.android.gms.location.DetectedActivity;

import java.util.Random;

public class Constants {

    //Steps chart initialize values
    public static final long STEP_CHART_START_VALUE = 0;
    public static final long STEP_CHART_FINAL_VALUE = 1000;


    //Authentication Process Variables
    public static final int REQUEST_CODE_OAUTH = 1;


    //Googel Fit API Process Variables
    public static final int SAMPLING_RATE = 10;
    public static final int AWAIT_VALUE = 1;
    public static final int WEEK_START_BEFORE = -7;
    public static final int WEEK_END_BEFORE = 0;
    public static final int TODAY_START_BEFORE = -50;
    public static final int TODAY_END_BEFORE = 0;
    public static final int YESTERDAY_START_BEFORE = -24;
    public static final int YESTERDAY_END_BEFORE = -23  ;


    //Tracking Activity Variables
    public static final int DEFAULT_ACTIVITY_TYPE = -1;
    public static final int DEFAULT_ACTIVITY_CONFIDENCE = 0;
    public static final int NOTIFICATION_CODE = 0;
    public static final long DETECTION_INTERVAL_MILLISECONDS = 3000;
    public static final int CONFIDENCE = 70;

    //Tracking Bmi Variables
    public static final int BMI_MINIMUM_VALUE = 18;
    public static final int BMI_MAXIMUM_VALUE = 25;
    public static final int BMI_COLOR_ALPHA = 255;
    public static final int BMI_COLOR_MAXIMUM = 256;


    //Date Bmi Variables
    public static final String BMI_WEEK_DAY = "EEEE";
    public static final String BMI_DAY = "dd";
    public static final String BMI_MONTH_STRING = "MMM";
    public static final String BMI_MONTH = "MM";
    public static final String BMI_YEAR = "yyyy";




    public static void displayToast(Context context, String text){

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }

    public static int identifyActvityTitle(int type){

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                return R.string.activity_in_vehicle;

            }
            case DetectedActivity.ON_BICYCLE: {
                return R.string.activity_on_bicycle;

            }
            case DetectedActivity.RUNNING: {
                return R.string.activity_running;

            }
            case DetectedActivity.WALKING: {
                return R.string.activity_walking;

            }
            case DetectedActivity.ON_FOOT: {
               return  R.string.activity_on_foot;

            }

            case DetectedActivity.STILL: {
                return R.string.activity_still;

            }
            case DetectedActivity.TILTING: {
                return R.string.activity_tilting;

            }

            case DetectedActivity.UNKNOWN: {
                return R.string.activity_unknown;

            }


        }

        return  R.string.activity_unknown;
    }

    public static int  identifyActvityIcon(int type){

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                return R.drawable.ic_driving;

            }
            case DetectedActivity.ON_BICYCLE: {
                return R.drawable.ic_on_bicycle;

            }
            case DetectedActivity.RUNNING: {
                return R.drawable.ic_running;

            }
            case DetectedActivity.WALKING: {
                return R.drawable.ic_walking;

            }
            case DetectedActivity.ON_FOOT: {
                return R.drawable.ic_walking;

            }

            case DetectedActivity.STILL: {
                return R.drawable.ic_still;

            }
            case DetectedActivity.TILTING: {
                return R.drawable.ic_tilting;

            }

            case DetectedActivity.UNKNOWN: {
                return R.drawable.ic_still;

            }


        }
        return  R.drawable.ic_still;
    }

    public static String getBmiValue(Double weight, Double height){
        Double  personBmi = weight/(height * height);
        return String.format("%.2f", personBmi) ;

    }

    public static String getBmiStatus(Context context, Double weight, Double height){
        Double  personBmi = weight/(height * height);
        if(personBmi >= BMI_MINIMUM_VALUE && personBmi <= BMI_MAXIMUM_VALUE){
           return context.getString(R.string.bmi_ideal_status);
        }
        else if(personBmi > BMI_MAXIMUM_VALUE){
            return context.getString(R.string.bmi_above_ideal_status);
        }
        else{
            return context.getString(R.string.bmi_below_ideal_status);
        }
    }
    public static int getColor() {
        Random randomValue = new Random();
        int randomColor = Color.argb(BMI_COLOR_ALPHA, randomValue.nextInt(BMI_COLOR_MAXIMUM), randomValue.nextInt(BMI_COLOR_MAXIMUM), randomValue.nextInt(BMI_COLOR_MAXIMUM));
        return randomColor;

    }
}
