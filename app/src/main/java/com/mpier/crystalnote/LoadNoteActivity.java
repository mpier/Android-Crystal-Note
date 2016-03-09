package com.mpier.crystalnote;

import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadNoteActivity extends ActionBarActivity
{
    boolean[] isVisible;
    TextView mNotAvailable;
    boolean isWidgetCalling = false;
    static MyAdapter mAdapter;
    GridView gridView;
    LoadNoteActivity lna = this;

    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_note);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        mNotAvailable = (TextView) findViewById(R.id.views_not_available);

        gridView = (GridView)findViewById(R.id.gridview);
        mAdapter = new MyAdapter(this);
        gridView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            isWidgetCalling = extras.getBoolean("WIDGET");
        }

        overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }

    private class MyAdapter extends BaseAdapter
    {
        private List<Item> items = new ArrayList<Item>();
        private LayoutInflater inflater;
        public File files[];

        public MyAdapter(Context context)
        {
            inflater = LayoutInflater.from(context);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            files = cw.getDir("imageDir", Context.MODE_PRIVATE).listFiles();
            isVisible = new boolean[files.length + 1];

            for(int i=0; i<files.length; i++) {
                items.add(new Item(files[i].getName(), i, files[i]));
            }

            if(files.length>0) {
                mNotAvailable.setVisibility(View.GONE);
            }
        }

        public void deleteItem(int position) {
            files[position].delete();
        }
        public String getName(Item temp) {
            return temp.getName();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return items.get(i).drawableId;
        }

        @Override
        public View getView(final int i, final View view, final ViewGroup viewGroup)
        {
            View v = view;
            final ImageView picture;
            final TextView name;
            final ImageView mIconDelete;
            final ImageView mItemBackground;

            if(v == null)
            {
                v = inflater.inflate(R.layout.gridview_item, viewGroup, false);
                v.setTag(R.id.item_background, v.findViewById(R.id.item_background));
                v.setTag(R.id.icon_delete, v.findViewById(R.id.icon_delete));
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
            }

            mItemBackground = (ImageView)v.getTag(R.id.item_background);
            mIconDelete = (ImageView)v.getTag(R.id.icon_delete);
            picture = (ImageView)v.getTag(R.id.picture);
            name = (TextView)v.getTag(R.id.text);

            final Item item = (Item)getItem(i);

            picture.setImageDrawable(item.mPicture);
            name.setText(item.name);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    if (isWidgetCalling) {
                        int mAppWidgetId;
                        Intent intent = getIntent();
                        Bundle extras = intent.getExtras();
                        if (extras != null) {
                            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                            RemoteViews rv = new RemoteViews(getApplicationContext().getPackageName(), R.layout.my_app_widget_layout);
                            rv.setImageViewBitmap(R.id.main_widget_view, ((BitmapDrawable) item.mPicture).getBitmap());
                            //rv.setTextViewText(R.id.widget_name, checkImportance(item));
                            MyAppWidgetProvider.views = rv;
                            AppWidgetManager.getInstance(getApplicationContext()).partiallyUpdateAppWidget(mAppWidgetId, MyAppWidgetProvider.views);
                        }
                        finish();
                    } else {
                        MainActivity.TheDrawingView.setBitmap(item.getName().toString());
                        MainActivity.TheDrawingView.invalidate();
                        finish();
                    }
                }

            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isVisible[i]) {
                        //if the view is selected, then count time to unselect it
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                isVisible[i] = false;
                                mItemBackground.setVisibility(View.INVISIBLE);
                                name.setVisibility(View.INVISIBLE);
                                mIconDelete.setVisibility(View.INVISIBLE);
                            }
                        };
                        handler.postDelayed(runnable, 1500);
                        /////////
                        isVisible[i] = true;
                        mItemBackground.setVisibility(View.VISIBLE);
                        name.setVisibility(View.VISIBLE);
                        mIconDelete.setVisibility(View.VISIBLE);
                        mIconDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                // path to /data/data/yourapp/app_data/imageDir
                                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                                File f = new File(directory, item.getName());
                                f.delete();
                                v.invalidate();
                                mAdapter.deleteItem(i);
                                mAdapter = new MyAdapter(getApplicationContext());
                                gridView.setAdapter(mAdapter);
                                gridView.invalidateViews();

                                /*if (cw.getDir("imageDir", Context.MODE_PRIVATE).length() == 0) {
                                    mNotAvailable.setVisibility(View.VISIBLE);
                                }*/
                            }
                        });
                    } else {
                        isVisible[i] = false;
                        mItemBackground.setVisibility(View.INVISIBLE);
                        name.setVisibility(View.INVISIBLE);
                        mIconDelete.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
            });
            return v;
        }
    }

    String checkImportance(Item item)
    {
        switch(((BitmapDrawable) item.mPicture).getPaint().getColor()){
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}