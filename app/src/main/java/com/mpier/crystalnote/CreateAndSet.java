package com.mpier.crystalnote;

import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RemoteViews;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CreateAndSet extends ActionBarActivity {

    static DrawingView TheDrawingView;
    String path = new String();
    boolean doubleBackToExitPressedOnce = false;

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.tap_twice_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TheDrawingView = (DrawingView) findViewById(R.id.single_touch_view);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_and_save, menu);
        return true;
    }


    private String save(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date now = new Date();
        File mypath=new File(directory,formatter.format(now));

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    String checkImportance()
    {
        switch(TheDrawingView.mPaint.getColor()){
            case Color.RED:
                return getString(R.string.important);
            case Color.CYAN:
                return getString(R.string.normal);
            case Color.BLACK:
                return getString(R.string.not_important);
            default:
                return new String();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_set_as_widget) {
            path = save(TheDrawingView.getCurrentBitmap());
            Toast t = Toast.makeText(getApplicationContext(), R.string.action_saved, Toast.LENGTH_SHORT);
            t.show();

            int mAppWidgetId;
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                RemoteViews rv = new RemoteViews(getApplicationContext().getPackageName(), R.layout.my_app_widget_layout);
                rv.setImageViewBitmap(R.id.main_widget_view, TheDrawingView.getCurrentBitmap());
                //rv.setTextViewText(R.id.widget_name, checkImportance());
                MyAppWidgetProvider.views = rv;
                AppWidgetManager.getInstance(getApplicationContext()).partiallyUpdateAppWidget(mAppWidgetId, MyAppWidgetProvider.views);
            }
            finish();
            return true;
        }
        if (id == R.id.action_save) {
            path = save(TheDrawingView.getCurrentBitmap());
            Toast t = Toast.makeText(getApplicationContext(), R.string.action_saved, Toast.LENGTH_SHORT);
            t.show();
            return true;
        }
        if (id == R.id.action_load) {
            //TheDrawingView.setBitmap();
            Intent intent = new Intent(this, LoadNoteActivity.class);
            startActivity(intent);
            //overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top);
            return true;
        }
        if (id == R.id.action_undo) {
            TheDrawingView.makeUndo();
            return true;
        }
        if (id == R.id.action_reset) {
            TheDrawingView.clear();
            return true;
        }
        if (id == R.id.action_set_important) {
            TheDrawingView.setColor(Color.RED);
            TheDrawingView.invalidate();
            return true;
        }
        if (id == R.id.action_set_normal) {
            TheDrawingView.setColor(Color.CYAN);
            TheDrawingView.invalidate();
            return true;
        }
        if (id == R.id.action_set_not_important) {
            TheDrawingView.setColor(Color.BLACK);
            TheDrawingView.invalidate();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

}
