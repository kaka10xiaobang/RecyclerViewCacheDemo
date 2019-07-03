package com.kaka.recyclerview;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Xiaoqi.
 * Date:2019-06-17 15:29.
 * Project:RecyclerViewDemo.
 */
public class RecyclerViewWrapper extends RecyclerView {
    private  LayoutListener layoutListener;

    public RecyclerViewWrapper(@NonNull Context context) {
        super(context);
    }

    public RecyclerViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setLayoutListener(LayoutListener layoutListener) {
        this.layoutListener = layoutListener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (layoutListener != null) {
            layoutListener.onBeforeLayout();
        }
        super.onLayout(changed, l, t, r, b);

        if (layoutListener != null) {
            layoutListener.onAfterLayout();
        }
    }

    public interface LayoutListener {

        void onBeforeLayout();

        void onAfterLayout();
    }

}
