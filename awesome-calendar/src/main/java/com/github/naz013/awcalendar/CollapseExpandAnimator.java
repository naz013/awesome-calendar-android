package com.github.naz013.awcalendar;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import hirondelle.date4j.DateTime;

class CollapseExpandAnimator extends Animator {

    private static final String TAG = "CollapseExpandAnimator";
    private static final long COLLAPSE_EXPAND_TIME = 500L;

    static final int STATE_EXPANDED = 3;
    static final int STATE_COLLAPSED = 4;

    private AwesomeCalendarView mView;
    private MonthCell mCell;

    private int mLastX;
    private int mLastY;

    private int mDistance;

    private int mState = STATE_EXPANDED;
    private OnStateListener mOnStateListener;
    private boolean mIsAnimating;
    @Nullable
    private android.animation.Animator mAnimator;

    CollapseExpandAnimator(AwesomeCalendarView view) {
        this.mView = view;
        setState(STATE_EXPANDED);
    }

    MonthCell getCell() {
        return mCell;
    }

    void setCell(MonthCell cell) {
        this.mCell = cell;
        Utils.log(TAG, "setCell: " + cell);
    }

    public void toggle(int x, int y) {
        if (getState() == STATE_COLLAPSED) {
            expand(x, y);
        } else {
            collapse(x, y);
        }
    }

    private void expand(int x, int y) {
        cancelAnimation();
        start(x, y);
        mDistance = mCell.getExpandDistance();
        if (mDistance > 0) {
            ValueAnimator animator = ValueAnimator.ofInt(mDistance, 0);
            animator.setDuration(COLLAPSE_EXPAND_TIME);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int speed = mDistance - (Integer) animation.getAnimatedValue();
                    mDistance -= speed;
                    animate(mLastX, mLastY + speed);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    mIsAnimating = false;
                    mAnimator = null;
                    setState(STATE_EXPANDED);
                }

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                    mIsAnimating = true;
                }
            });
            animator.start();
            mAnimator = animator;
        } else {
            setState(STATE_EXPANDED);
        }
    }

    private void collapse(int x, int y) {
        cancelAnimation();
        start(x, y);
        mDistance = mCell.getCollapseDistance();
        if (mDistance > 0) {
            ValueAnimator animator = ValueAnimator.ofInt(mDistance, 0);
            animator.setDuration(COLLAPSE_EXPAND_TIME);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int speed = mDistance - (Integer) animation.getAnimatedValue();
                    mDistance -= speed;
                    animate(mLastX, mLastY - speed);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    mIsAnimating = false;
                    mAnimator = null;
                    setState(STATE_COLLAPSED);
                }

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                    mIsAnimating = true;
                }
            });
            animator.start();
            mAnimator = animator;
        } else {
            setState(STATE_COLLAPSED);
        }
    }

    @Override
    public void start(int x, int y) {
        this.mLastX = x;
        this.mLastY = y;
    }

    @Override
    public void finishAnimation(int x, int y) {
        if (y - mLastY > 0) {
            expand(x, y);
        } else {
            collapse(x, y);
        }
    }

    @Override
    public void animate(int x, int y) {
        int offset = y - mLastY;
        mCell.setOffsetY(offset);
        mLastY = y;
        mView.getLayoutParams().height = mCell.getBottom() - mCell.getTop();
        mView.requestLayout();
    }

    @Override
    public boolean cancelAnimation() {
        if (mIsAnimating && mAnimator != null) {
            mAnimator.cancel();
        }
        return false;
    }

    void setState(int state) {
        this.mState = state;
        if (mOnStateListener != null) {
            mOnStateListener.onStateChanged(state);
        }
    }

    int getState() {
        return mState;
    }

    void setOnStateListener(OnStateListener onStateListener) {
        this.mOnStateListener = onStateListener;
    }

    public OnStateListener getOnStateListener() {
        return mOnStateListener;
    }

    @Override
    public void onDraw(Canvas canvas, Painter painter) {
        mCell.onDraw(canvas, painter);
    }

    @Override
    public DateTime getClicked(int x, int y) {
        return mCell.get(x, y);
    }

    @Override
    public boolean isEmpty() {
        return mCell == null;
    }

    interface OnStateListener {
        void onStateChanged(int state);
    }
}
