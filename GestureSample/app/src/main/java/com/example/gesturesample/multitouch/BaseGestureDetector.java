package com.example.gesturesample.multitouch;

import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by HenryHo on 2017/5/16.
 */

public abstract class BaseGestureDetector {

    private Context context;
    private boolean gestureInProgress;

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

    private void updateStateByEvent(MotionEvent curr) {

    }

    private void resetState() {

    }

}
