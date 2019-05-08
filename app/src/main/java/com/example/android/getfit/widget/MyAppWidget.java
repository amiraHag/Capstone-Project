package com.example.android.getfit.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.getfit.R;
import com.example.android.getfit.ui.BMIActivity;
import com.example.android.getfit.db.Bmi;

import io.paperdb.Paper;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Init Paper DB
        Paper.init(context);

        //Read Widget content from paper db

        Bmi mWidgetBMIvalue = Paper.book().read(context.getString(R.string.widget_paper_bmi_value));
        Boolean mWidgetContentStatus =  Paper.book().read(context.getString(R.string.widget_paper_content_status));

        // Create an Intent to launch ExampleActivity
        Intent intent = new Intent(context, BMIActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);
        if(mWidgetBMIvalue != null && mWidgetContentStatus) {
            views.setTextViewText(R.id.tv_widget_bmivalue, context.getResources().getString(R.string.widget_your_bmi_title) + mWidgetBMIvalue.getBmiValue());
            views.setTextViewText(R.id.tv_widget_bmistatus, mWidgetBMIvalue.getBmiStatusDescription());
        }
        else{
            views.setTextViewText(R.id.tv_widget_bmivalue, context.getString(R.string.widget_clear_value));
            views.setTextViewText(R.id.tv_widget_bmistatus, context.getString(R.string.widget_clear_status));
        }
        //Attach an on-click listener to the button
        views.setOnClickPendingIntent(R.id.bt_widget_bmi, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

