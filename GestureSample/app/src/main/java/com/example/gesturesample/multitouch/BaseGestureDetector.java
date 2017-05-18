package com.example.gesturesample.multitouch;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by HenryHo on 2017/5/16.
 */

public abstract class BaseGestureDetector {
    //protected的變數都可以讓繼承這個class的孩子直接使用
    private Context context;
    protected boolean gestureInProgress;

    protected MotionEvent prevEvent;
    protected MotionEvent currEvent;

    protected long timeDelta;

    protected float currPressure;//這個用來更精確的判斷按壓狀況,沒使用也沒關係
    protected float prevPressure;//這個用來更精確的判斷按壓狀況,沒使用也沒關係
    protected static final float PRESSURE_THRESHOLD = 0.67f;//這個閥值用來更精確的判斷按壓狀況,沒使用也沒關係


    public BaseGestureDetector(Context context) {
        this.context = context;
    }

    public void onTouchEvent(MotionEvent event) {
        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;

        if (!gestureInProgress) {
            handleStartProgressEvent(actionCode, event);
        } else {
            handleInProgressEvent(actionCode, event);
        }
    }

    //如果要讓人複寫method, 又不用interface, 那就要宣告成abstract, 所以整個BaseGestureDetector就得宣告成abstract
    protected abstract void handleStartProgressEvent(int actionCode, MotionEvent event);
    protected abstract void handleInProgressEvent(int actionCode, MotionEvent event);

    protected void updateStateByEvent(MotionEvent curr) {
        MotionEvent prev = prevEvent;

        // Reset mCurrEvent
        if (currEvent != null) {
            currEvent.recycle();
            currEvent = null;
        }
        currEvent = MotionEvent.obtain(curr);


        // Delta time
        timeDelta = curr.getEventTime() - prev.getEventTime();

        // Pressure,這只是用來更精確的判斷手指按壓狀況,所以即便沒有使用也沒關係
        currPressure = curr.getPressure(curr.getActionIndex());
        prevPressure = prev.getPressure(prev.getActionIndex());
    }

    protected void resetState() {
        if (prevEvent != null) {
            prevEvent.recycle();
            prevEvent = null;
        }
        if (currEvent != null) {
            currEvent.recycle();
            currEvent = null;
        }

        gestureInProgress = false;
    }

}
