package com.mpier.crystalnote;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class DrawingView extends View {

    private Bitmap  mBackgroundBitmap;
    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    public Paint   mPaint;
    private List<Path>  mPathsArray = new ArrayList<Path>();

    public DrawingView(Context c, AttributeSet attrs) {
        super(c, attrs);

        mPaint = new Paint();
        mPaint.setColor(Color.CYAN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);

        mPath = new Path();
        mBitmapPaint = new Paint();

        mBitmapPaint.setDither(true);
    }

    public void setBitmap(String name) {
        try {
            clear();
            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            mCanvas.drawBitmap(b, 0, 0, mBitmapPaint);
            mBackgroundBitmap = b;
            invalidate();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public Bitmap getCurrentBitmap() {
        return mBitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void setmBackgroundBitmap() {
        mBackgroundBitmap = mBitmap;
    }
    public void clear(){
        mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPathsArray = new ArrayList<Path>();
        mPath = new Path();
        invalidate();
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        for(Path p : mPathsArray)
            mCanvas.drawPath(p, mPaint);
        invalidate();
    }

    public boolean makeUndo() {
        if(mPathsArray.size()>=1) {
            mPathsArray.remove(mPathsArray.size() - 1);
            if(mBackgroundBitmap!=null) {
                mCanvas.drawBitmap(mBackgroundBitmap, 0, 0, mBitmapPaint);
            } else {
                mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBitmap);
            }
            for(Path p : mPathsArray)
                mCanvas.drawPath(p, mPaint);
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = -1;

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPathsArray.add(mPath);
        mPath = new Path();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}