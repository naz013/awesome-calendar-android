package com.github.naz013.awcalendar;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import hirondelle.date4j.DateTime;

class PageSlideAnimator extends Animator {

    static final int STATE_SLIDE_RIGHT = 1;
    static final int STATE_SLIDE_LEFT = 2;
    private static final int STATE_IDLE = 0;

    private static final long SLIDE_TIME = 500L;
    private static final String TAG = "PageSlideAnimator";

    private ContainerCell mPrevCell;
    private ContainerCell mCurrentCell;
    private ContainerCell mNextCell;
    private List<DayCell> mCells = new ArrayList<>();

    private int mLastX;
    private int mLastY;
    private AwesomeCalendarView mView;

    private int mDistance;
    private boolean mIsAnimating;

    private int mState;
    private OnStateListener mOnStateListener;

    @Nullable
    private android.animation.Animator mAnimator;

    PageSlideAnimator(AwesomeCalendarView awesomeCalendarView) {
        this.mView = awesomeCalendarView;
        setState(STATE_IDLE);
    }

    ContainerCell getCurrent() {
        return mCurrentCell;
    }

    ContainerCell getPrevious() {
        return mPrevCell;
    }

    ContainerCell getNext() {
        return mNextCell;
    }

    void setCells(ContainerCell prevCell, ContainerCell currentCell, ContainerCell nextCell) {
        this.mPrevCell = prevCell;
        this.mCurrentCell = currentCell;
        this.mNextCell = nextCell;
        this.mCells.clear();
        this.mCells.addAll(prevCell.getCells());
        this.mCells.addAll(currentCell.getCells());
        this.mCells.addAll(nextCell.getCells());
        Utils.log(TAG, "setCells: p " + mPrevCell);
        Utils.log(TAG, "setCells: c " + mCurrentCell);
        Utils.log(TAG, "setCells: n " + mNextCell);
    }

    private void slide(int x, int y) {
        boolean slideLeft = mLastX > x;
        start(x, y);
        int dC = Math.abs(mCurrentCell.getLeft());
        int dP = Math.abs(mPrevCell.getLeft());
        int dN = Math.abs(mNextCell.getLeft());
        Utils.log(TAG, "slide: " + slideLeft + ", " + dC + ", " + dP + ", " + dN);
        if (slideLeft) {
            if (dN < dC || mCurrentCell.getLeft() < 0) {
                mDistance = mNextCell.getLeft();
            } else {
                mDistance = mCurrentCell.getLeft();
            }
            mDistance = Math.abs(mDistance);
            if (mDistance % 30 != 0) {
                int off = (mDistance % 30);
                mDistance -= off;
                animate(mLastX - off, mLastY);
            }
            ValueAnimator animator = ValueAnimator.ofInt(mDistance, 0);
            animator.setDuration(SLIDE_TIME);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int speed = mDistance - (Integer) animation.getAnimatedValue();
                    mDistance -= speed;
                    animate(mLastX - speed, mLastY);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    mIsAnimating = false;
                    mAnimator = null;
                    if (mPrevCell.getLeft() == 0) {
                        setState(STATE_SLIDE_LEFT);
                    } else if (mNextCell.getLeft() == 0) {
                        setState(STATE_SLIDE_RIGHT);
                    } else {
                        setState(STATE_IDLE);
                    }
                }

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                    mIsAnimating = true;
                }
            });
            animator.start();
            mAnimator = animator;
        } else {
            if (dP < dC || mCurrentCell.getLeft() > 0) {
                mDistance = mPrevCell.getLeft();
            } else {
                mDistance = mCurrentCell.getLeft();
            }
            mDistance = Math.abs(mDistance);
            if (mDistance % 30 != 0) {
                int off = (mDistance % 30);
                mDistance -= off;
                animate(mLastX + off, mLastY);
            }
            ValueAnimator animator = ValueAnimator.ofInt(mDistance, 0);
            animator.setDuration(SLIDE_TIME);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int speed = mDistance - (Integer) animation.getAnimatedValue();
                    mDistance -= speed;
                    animate(mLastX + speed, mLastY);
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    mIsAnimating = false;
                    mAnimator = null;
                    if (mPrevCell.getLeft() == 0) {
                        setState(STATE_SLIDE_LEFT);
                    } else if (mNextCell.getLeft() == 0) {
                        setState(STATE_SLIDE_RIGHT);
                    } else {
                        setState(STATE_IDLE);
                    }
                }

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                    mIsAnimating = true;
                }
            });
            animator.start();
            mAnimator = animator;
        }
    }

    @Override
    public void start(int x, int y) {
        this.mLastX = x;
        this.mLastY = y;
    }

    @Override
    public void finishAnimation(int x, int y) {
        slide(x, y);
    }

    @Override
    public void animate(int x, int y) {
        int offset = x - mLastX;
        mPrevCell.setOffsetX(offset);
        mCurrentCell.setOffsetX(offset);
        mNextCell.setOffsetX(offset);
        mLastX = x;
        mView.invalidate();
    }

    @Override
    public boolean cancelAnimation() {
        if (mIsAnimating && mAnimator != null) {
            mAnimator.cancel();
            return true;
        }
        return false;
    }

    private void setState(int state) {
        this.mState = state;
        if (mOnStateListener != null) {
            mOnStateListener.onStateChanged(state);
        }
    }

    public int getState() {
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
        for (DayCell dayCell : mCells) {
            dayCell.onDraw(canvas, painter);
        }
    }

    @Override
    public DateTime getClicked(int x, int y) {
        return mCurrentCell.get(x, y);
    }

    @Override
    public boolean isEmpty() {
        return mPrevCell == null || mCurrentCell == null || mNextCell == null;
    }

    public interface OnStateListener {
        void onStateChanged(int state);
    }
}
