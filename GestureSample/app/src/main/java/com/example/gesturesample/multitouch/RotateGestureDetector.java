package com.example.gesturesample.multitouch;

import android.content.Context;
import android.view.MotionEvent;

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

public class RotateGestureDetector extends TwoFingerGestureDetector {

    public interface OnRotateGestureListener {
        boolean onRotate(RotateGestureDetector detector);
        boolean onRotateBegin(RotateGestureDetector detector);
        void onRotateEnd(RotateGestureDetector detector);
    }

    private OnRotateGestureListener listener;
    private boolean sloppyGesture;

    public RotateGestureDetector(Context context, OnRotateGestureListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_DOWN:
                // At least the second finger is on screen now

                resetState(); // In case we missed an UP/CANCEL event
                prevEvent = MotionEvent.obtain(event);
                timeDelta = 0;

                updateStateByEvent(event);

                // See if we have a sloppy gesture
                sloppyGesture = isSloppyGesture(event);
                if(!sloppyGesture){
                    // No, start gesture now
                    gestureInProgress = listener.onRotateBegin(this);//true
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (!sloppyGesture) {
                    break;
                }

                // See if we still have a sloppy gesture
                sloppyGesture = isSloppyGesture(event);
                if(sloppyGesture){
                    // No, start normal gesture now
                    gestureInProgress = listener.onRotateBegin(this);//true
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (!sloppyGesture) {
                    break;
                }

                break;
        }
    }

    @Override
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_POINTER_UP:
                // Gesture ended but
                updateStateByEvent(event);

                if (!sloppyGesture) {
                    listener.onRotateEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_CANCEL:
                if (!sloppyGesture) {
                    listener.onRotateEnd(this);
                }

                resetState();
                break;

            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);

                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (currPressure / prevPressure > PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = listener.onRotate(this);
                    if (updatePrevious) {
                        prevEvent.recycle();
                        prevEvent = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    @Override
    protected void resetState() {
        super.resetState();
        sloppyGesture = false;
    }

    public float getRotationDegreesDelta() {
        double diffRadians = Math.atan2(prevFingerDiffY, prevFingerDiffX) - Math.atan2(currFingerDiffY, currFingerDiffX);
        return (float) (diffRadians * 180 / Math.PI);
    }
}