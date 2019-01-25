package com.keepshare.sweepitemlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by BUG君 on 2019/1/23.
 * <p>
 * item 侧滑
 */
public class SweepItemLayout extends LinearLayout {

    private static final String TAG = "SweepItemLayout";

    private static final int MOVE_STATE = 1;
    private static final int OPEN_STATE = 2;
    private static final int CLOSE_STATE = 3;

    private VelocityTracker mVelocityTracker;
    private ViewDragHelper mDragHelper;

    private int moveDistance;

    private int currentState = CLOSE_STATE;

    private float mLastX;
    private float mLastY;

    private float sensitivity = 0.8f;

    private float slope = 0.5f;

    private boolean hasIntercept = false;

    private static SweepItemLayout mOpenItem;


    public SweepItemLayout(Context context) {
        this(context, null);
    }

    public SweepItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SweepItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
        mDragHelper.setMinVelocity(1000);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int childCount = getChildCount();
        if (childCount > 2) {
            throw new RuntimeException("the count if layout child is can not exceed two");
        }
        moveDistance = obtainMoveDistance(childCount);
    }

    private float mLastDispatchX;
    private float mLastDispatchY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ev.setLocation(ev.getX() * sensitivity, ev.getY() * sensitivity);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                closeOpenItem();
                mDragHelper.abort();

                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) (x - mLastDispatchX);
                int moveY = (int) (y - mLastDispatchY);
                if (Math.abs(moveY) * slope >= Math.abs(moveX)
                        && !hasIntercept
                        && !mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL)
                        && mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastX = 0;
                mLastY = 0;
                break;
        }
        mLastDispatchX = x;
        mLastDispatchY = y;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        final int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mDragHelper.processTouchEvent(ev);
            shouldIntercept = false;
        }

        if (action == MotionEvent.ACTION_MOVE) {

            int deltaX = (int) (x - mLastX);
            int deltaY = (int) (y - mLastY);
            shouldIntercept = Math.abs(deltaX) * slope > Math.abs(deltaY)
                    && mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL);
            if (!hasIntercept) {
                hasIntercept = shouldIntercept;
            }
        }

        mLastX = x;
        mLastY = y;

        return shouldIntercept;
    }

    public int getCurrentState() {
        return currentState;
    }

    protected void resetStats() {
        this.currentState = CLOSE_STATE;
        this.mOpenItem = null;
        mDragHelper.cancel();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
        mDragHelper.processTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            hasIntercept = false;
        }

        return true;
    }

    private void closeOpenItem() {
        if (mOpenItem == null
                || mOpenItem == this) return;
//
//        if (mOpenItem.getCurrentState() == OPEN_STATE) {
//            mOpenItem.scrollBy(-moveDistance, 0);
//            mOpenItem.resetStats();
////            mOpenItem.close();
//
//            postInvalidate();
//        }
    }

    public void close() {
        if (currentState != OPEN_STATE) return;
//        this.scrollBy(-moveDistance, 0);
        postInvalidate();
    }

    private void closeSmooth() {

    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view == getChildAt(0);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            final float distance = Math.abs(releasedChild.getLeft());

            mVelocityTracker.computeCurrentVelocity(2000);
            if (Math.abs(mVelocityTracker.getXVelocity()) > 8000) {
                mDragHelper.flingCapturedView(-moveDistance, 0, 0, 0);
                invalidate();
                return;
            }

            if (distance >= moveDistance / 4) {
                mDragHelper.settleCapturedViewAt(-moveDistance, 0);
            } else {
                mDragHelper.settleCapturedViewAt(0, 0);
            }
            invalidate();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            int childCount = getChildCount();
            if (childCount < 2) return;
            final View hideView = getChildAt(1);
            hideView.setTranslationX(left);
            changeStatus(Math.abs(left));
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            final int leftBound = getPaddingLeft() - moveDistance;
            final int rightBound = 0;
            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

            return newLeft;
        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void changeStatus(int newLeft) {
        currentState = MOVE_STATE;
        if (newLeft >= moveDistance) {
            currentState = OPEN_STATE;
            mOpenItem = SweepItemLayout.this;
        }

        if (newLeft <= 0) {
            currentState = CLOSE_STATE;
            mOpenItem = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    private int obtainMoveDistance(int childCount) {
        if (childCount < 2) return 0;
        final View hideView = getChildAt(1);
        return hideView.getMeasuredWidth();
    }

    private void release() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }
}
