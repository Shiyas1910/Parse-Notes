package com.example.mohamedshiyas.parsenoteapp;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by mohamedshiyas on 08/08/17.
 */
public class NoteItemTouchCallback extends ItemTouchHelper.SimpleCallback {
    private static final float ALPHA_FULL = 1.0f;
    private DeletionListener listener;

    private NoteItemTouchCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public NoteItemTouchCallback(int dragDirs, int swipeDirs, DeletionListener deletionListener) {
        super(dragDirs, swipeDirs);
        this.listener = deletionListener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null){
            listener.itemRemoved(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
        else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY,actionState, isCurrentlyActive);
        }
    }
}
