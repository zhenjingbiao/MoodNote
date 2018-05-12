package com.example.jingbiaozhen.moodnote;
/*
 * Created by jingbiaozhen on 2018/5/12.
 **/

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class DividerGridItemDecoration extends RecyclerView.ItemDecoration
{

    public DividerGridItemDecoration(Activity mainActivity)
    {
        super();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    @Deprecated
    public void onDraw(Canvas c, RecyclerView parent)
    {
        super.onDraw(c, parent);

    }
}
