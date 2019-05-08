package com.example.android.getfit.utils;


import android.arch.persistence.room.TypeConverter;
import android.text.format.DateFormat;


import java.util.Date;

public class DateConverter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTime(Date date) {
        return date == null ? null : date.getTime();
    }


    public static String getDayMonth(Date date) {
        String dayOfTheWeek = (String) DateFormat.format(Constants.BMI_WEEK_DAY, date); // Sunday
        String day = (String) DateFormat.format(Constants.BMI_DAY, date); // 5
        String monthString = (String) DateFormat.format(Constants.BMI_MONTH_STRING, date); // May
        String monthNumber  = (String) DateFormat.format(Constants.BMI_MONTH,   date); // 05
        String year         = (String) DateFormat.format(Constants.BMI_YEAR, date); // 2019

        return dayOfTheWeek + " " +  day + " / "+  monthString  + " " + monthNumber + " / "+  year;
    }
}
