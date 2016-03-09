package com.mpier.crystalnote;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Marek on 2015-11-03.
 */
public class AppRater {
    private final static String APP_TITLE = "Crystal Note";// App Name
    private final static String APP_PNAME = "com.mpier.crystalnote";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setTitle(mContext.getString(R.string.rate) + APP_TITLE);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(10, 10, 10, 10);

        TextView tv = new TextView(mContext);
        tv.setText(mContext.getString(R.string.rate_long1) + APP_TITLE + mContext.getString(R.string.rate_long2));
        tv.setTextSize(20);
        //tv.setWidth(240);
        tv.setPadding(0, 5, 0, 5);
        ll.addView(tv);

        LinearLayout ll2 = new LinearLayout(mContext);
        ll2.setOrientation(LinearLayout.VERTICAL);

        Button b1 = new Button(mContext);
        b1.setText(mContext.getString(R.string.rate) + APP_TITLE);
        b1.setBackgroundColor(Color.GREEN);
        b1.setSingleLine(true);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });
        ll2.addView(b1);

        Button b2 = new Button(mContext);
        b2.setText(mContext.getString(R.string.rate_remind));
        b2.setSingleLine(true);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ll2.addView(b2);

        Button b3 = new Button(mContext);
        b3.setText(mContext.getString(R.string.rate_dismiss));
        b3.setSingleLine(true);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        ll2.addView(b3);
        ll.addView(ll2);

        dialog.setContentView(ll);
        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
    }
}