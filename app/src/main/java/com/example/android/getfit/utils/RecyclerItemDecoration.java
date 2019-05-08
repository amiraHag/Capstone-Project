package com.example.android.getfit.utils;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerItemDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffset;

    public RecyclerItemDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = mItemOffset;
        outRect.left = mItemOffset;
        outRect.right = mItemOffset;
        outRect.bottom = mItemOffset;
    }
}
