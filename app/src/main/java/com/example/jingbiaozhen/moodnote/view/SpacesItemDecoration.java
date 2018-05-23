package com.example.jingbiaozhen.moodnote.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/*
 * Created by jingbiaozhen on 2018/5/23.
 * LinearLayoutManager spacing
 **/

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.left = space;
        } else {
            outRect.left = 0;
        }
    }
}
