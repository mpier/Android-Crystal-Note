package com.mpier.crystalnote;

/**
 * Created by Marek on 2015-10-19.
 */

import java.util.Arrays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MyAppWidgetProvider extends android.appwidget.AppWidgetProvider {
    static RemoteViews views;
    static String name = new String();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        Log.i("ExampleWidget",  "Updating widgets " + Arrays.asList(appWidgetIds));

        // Perform this loop procedure for each App Widget that belongs to this
        // provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            // Create an Intent to launch ExampleActivity
            Intent createAndSetIntent = new Intent(context, CreateAndSet.class);
            createAndSetIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            createAndSetIntent.putExtra("WIDGET", true);
            createAndSetIntent.putExtra("NAME", name);
            createAndSetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            createAndSetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent createAndSetPendingIntent = PendingIntent.getActivity(context, appWidgetId, createAndSetIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create an Intent to launch ExampleActivity
            Intent createIntent = new Intent(context, MainActivity.class);
            createIntent.putExtra("WIDGET", true);
            createIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent createPendingIntent = PendingIntent.getActivity(context, appWidgetId, createIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, LoadNoteActivity.class);
            intent.putExtra("WIDGET", true);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget_layout);
            //views.setOnClickPendingIntent(R.id.main_widget_view, createAndSetPendingIntent);
            views.setOnClickPendingIntent(R.id.icon_edit, createAndSetPendingIntent);
            views.setOnClickPendingIntent(R.id.icon_home, createPendingIntent);
            views.setOnClickPendingIntent(R.id.icon_settings, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}