package com.example.gesturesample.multitouch;

import android.content.Context;
import android.graphics.PointF;
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

public class MoveGestureDetector extends BaseGestureDetector{

    public interface OnMoveGestureListener {
        boolean onMove(MoveGestureDetector detector);
        boolean onMoveBegin(MoveGestureDetector detector);
        void onMoveEnd(MoveGestureDetector detector);
    }

    public static class SimpleOnMoveGestureListener implements OnMoveGestureListener {
        public boolean onMove(MoveGestureDetector detector) {
            return false;
        }

        public boolean onMoveBegin(MoveGestureDetector detector) {
            return true;
        }

        public void onMoveEnd(MoveGestureDetector detector) {
        }
    }


    private static final PointF FOCUS_DELTA_ZERO = new PointF();
    private OnMoveGestureListener listener;

//    private PointF currFocusInternal;
//    private PointF prevFocusInternal;
//    private PointF focusExternal = new PointF();
//    private PointF focusDeltaExternal = new PointF();

    public MoveGestureDetector(Context context, OnMoveGestureListener listener) {
        super(context);//注意這邊就不需要再this.context = context了,直接super(context)即可
        this.listener = listener;
    }

    @Override
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {

    }

    @Override
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {

    }

}
