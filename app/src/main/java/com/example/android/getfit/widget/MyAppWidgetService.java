package com.example.android.getfit.widget;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.getfit.R;
import com.example.android.getfit.db.Bmi;
import com.example.android.getfit.widget.MyAppWidget;

import io.paperdb.Paper;

public class MyAppWidgetService extends IntentService {


    public MyAppWidgetService(String name) {
        super(name);
    }

    public MyAppWidgetService() {
        super(String.valueOf(R.string.widget_app_service));
    }

    public static void initWidgetContent(Context context, Bmi bmi, Boolean widgetClear) {
        Paper.book().write(context.getString(R.string.widget_paper_bmi_value), bmi);
        Paper.book().write(context.getString(R.string.widget_paper_content_status), widgetClear);
        initAppWidget(context);
    }

    public static void initEmptyWidgetContent(Context context, Boolean widgetClear) {
        Paper.book().write(context.getString(R.string.widget_paper_content_status), widgetClear);

        initAppWidget(context);
    }

    public static void initAppWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyAppWidget.class));
        MyAppWidget.updateAppWidgets(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
