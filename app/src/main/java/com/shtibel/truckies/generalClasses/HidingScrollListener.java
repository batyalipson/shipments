package com.shtibel.truckies.generalClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.shtibel.truckies.R;

/**
 * Created by Shtibel on 04/08/2016.
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
    private static final float HIDE_THRESHOLD = 10;
    private static final float SHOW_THRESHOLD = 70;

    private int mToolbarOffset = 0;
    private boolean mControlsVisible = true;
    private int mToolbarHeight;

    public HidingScrollListener(Context context) {
        mToolbarHeight = (int)context.getResources().getDimension(R.dimen.tabs_title_height)+
                (int)(context.getResources().getDimension(R.dimen.medium_margin)*2);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (mControlsVisible) {
                if (mToolbarOffset > HIDE_THRESHOLD) {
                    setInvisible();
                } else {
                    setVisible();
                }
            } else {
                if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                    setVisible();
                } else {
                    setInvisible();
                }
            }
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset);

        if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {
            mToolbarOffset += dy;
        }

    }

    private void clipToolbarOffset() {
        if(mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    public void setVisible() {
        if(mToolbarOffset > 0) {
            onShow();
            mToolbarOffset = 0;
        }
        mControlsVisible = true;
    }

    private void setInvisible() {
        if(mToolbarOffset < mToolbarHeight) {
            onHide();
            mToolbarOffset = mToolbarHeight;
        }
        mControlsVisible = false;
    }

    public int getmToolbarOffset() {
        return mToolbarOffset;
    }

    public void setmToolbarOffset(int mToolbarOffset) {
        this.mToolbarOffset = mToolbarOffset;
    }

    public abstract void onMoved(int distance);
    public abstract void onShow();
    public abstract void onHide();
}