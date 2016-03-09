package com.mpier.crystalnote;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.File;

/**
 * Created by Marek on 2015-10-22.
 */
public class Item
{
    final String name;
    final int drawableId;
    final Drawable mPicture;

    Item(String name, int drawableId, File file)
    {
        this.name = name;
        this.drawableId = drawableId;
        this.mPicture = Drawable.createFromPath(file.getAbsolutePath());
    }

    String getName() {
        return name;
    }

}

