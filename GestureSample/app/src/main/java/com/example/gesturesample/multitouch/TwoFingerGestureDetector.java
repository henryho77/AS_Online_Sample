package com.example.gesturesample.multitouch;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * @author Almer Thie (code.almeros.com)
 * Copyright (c) 2013, Almer Thie (code.almeros.com)
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer
 *  in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

public abstract class TwoFingerGestureDetector extends BaseGestureDetector {

    private final float edgeSlop;//斜率
    private float rightSlopEdge;
    private float bottomSlopEdge;

    protected float prevFingerDiffX;
    protected float prevFingerDiffY;
    protected float currFingerDiffX;
    protected float currFingerDiffY;

    private float currLen;
    private float prevLen;

    //Create constructor matching super
    public TwoFingerGestureDetector(Context context) {
        super(context);
        edgeSlop = ViewConfiguration.get(context).getScaledDoubleTapSlop();
    }

    //自行補上abstract
    @Override
    protected abstract void handleStartProgressEvent(int actionCode, MotionEvent event);

    @Override
    protected abstract void handleInProgressEvent(int actionCode, MotionEvent event);

    @Override
    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);

        final MotionEvent prev = prevEvent;//prevEvent來自BaseGestureDetector

        currLen = -1;
        prevLen = -1;

        // Previous
        final float px0 = prev.getX(0);//第一根指頭的x座標
        final float py0 = prev.getY(0);//第一根指頭的y座標
        final float px1 = prev.getX(1);//第二根指頭的x座標
        final float py1 = prev.getY(1);//第二根指頭的y座標
        final float pvx = px1 - px0;//x方向兩根指頭的差距
        final float pvy = py1 - py0;//y方向兩根指頭的差距
        prevFingerDiffX = pvx;
        prevFingerDiffY = pvy;

        // Current
        final float cx0 = curr.getX(0);
        final float cy0 = curr.getY(0);
        final float cx1 = curr.getX(1);
        final float cy1 = curr.getY(1);
        final float cvx = cx1 - cx0;
        final float cvy = cy1 - cy0;
        currFingerDiffX = cvx;
        currFingerDiffY = cvy;
    }

    public float getCurrentSpan() {
        if (currLen == -1) {
            final float cvx = currFingerDiffX;
            final float cvy = currFingerDiffY;
            currLen = (float) Math.sqrt(cvx * cvx + cvy * cvy);
        }
        return currLen;
    }

    public float getPreviousSpan() {
        if (prevLen == -1) {
            final float pvx = prevFingerDiffX;
            final float pvy = prevFingerDiffY;
            prevLen = (float) Math.sqrt(pvx * pvx + pvy * pvy);
        }
        return prevLen;
    }

    protected static float getRawX(MotionEvent event, int pointerIndex) {
        float offset = event.getX() - event.getRawX();
        if(pointerIndex < event.getPointerCount()){
            return event.getX(pointerIndex) + offset;
        }
        return 0f;
    }

    protected static float getRawY(MotionEvent event, int pointerIndex) {
        float offset = event.getY() - event.getRawY();
        if(pointerIndex < event.getPointerCount()){
            return event.getY(pointerIndex) + offset;
        }
        return 0f;
    }

    protected boolean isSloppyGesture(MotionEvent event) {
        // As orientation can change, query the metrics in touch down
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();//context來自BaseGestureDetector
        rightSlopEdge = metrics.widthPixels - edgeSlop;//算出右邊斜率
        bottomSlopEdge = metrics.heightPixels - edgeSlop;//算出底部斜率

        final float edgeSlop = this.edgeSlop;
        final float rightSlop = rightSlopEdge;
        final float bottomSlop = bottomSlopEdge;

        final float x0 = event.getRawX();
        final float y0 = event.getRawY();
        final float x1 = getRawX(event, 1);
        final float y1 = getRawY(event, 1);

        boolean p0sloppy = x0 < edgeSlop || y0 < edgeSlop || x0 > rightSlop || y0 > bottomSlop;
        boolean p1sloppy = x1 < edgeSlop || y1 < edgeSlop || x1 > rightSlop || y1 > bottomSlop;

        if (p0sloppy && p1sloppy) {
            return true;
        } else if (p0sloppy) {
            return true;
        } else if (p1sloppy) {
            return true;
        }
        return false;
    }

}
