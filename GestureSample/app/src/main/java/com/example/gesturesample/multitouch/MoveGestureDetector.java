package com.example.gesturesample.multitouch;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

import com.example.gesturesample.app.Config;

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

    private static final PointF FOCUS_DELTA_ZERO = new PointF();
    private OnMoveGestureListener listener;

    private PointF prevFocusInternal;//internal代表手指頭之間的中心點(prev)
    private PointF currFocusInternal;//internal代表手指頭之間的中心點(curr)
    private PointF focusExternal = new PointF();//
    private PointF focusDeltaExternal = new PointF();//external代表internal中心點找好後,前(prev)跟後(curr)internal中心點的平移量

    public MoveGestureDetector(Context context, OnMoveGestureListener listener) {
        super(context);//注意這邊就不需要再this.context = context了,直接super(context)即可
        this.listener = listener;
    }

    @Override
    protected void handleStartProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                resetState(); // In case we missed an UP/CANCEL event
                prevEvent = MotionEvent.obtain(event);
                timeDelta = 0;
                updateStateByEvent(event);
                break;

            case MotionEvent.ACTION_MOVE:
                //第一次的ACTION_MOVE會進來這邊,只做一個動作,把gestureInProgress設true,好讓移動中的動作都進到下面的handleInProgressEvent
                gestureInProgress = listener.onMoveBegin(this);//gestureInProgress 來自BaseGestureDetector
                break;
        }
    }

    @Override
    protected void handleInProgressEvent(int actionCode, MotionEvent event) {
        switch (actionCode) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                listener.onMoveEnd(this);//目前是沒做任何動作,保留用的
                resetState();//ACTION_UP或ACTION_CANCEL代表手放掉了,所有的移動已經結束,就用resetState()將gestureInProgress設回false,好讓下一次點擊時又能從handleStartProgressEvent開始
                break;

            case MotionEvent.ACTION_MOVE:
                //第一次之後的所有ACTION_MOVE會進來這邊
                updateStateByEvent(event);//更新移動的中心點

                //下面這段是用來更精確的判斷手指按壓狀況,不使用也沒關係
                // Only accept the event if our relative pressure is within
                // a certain limit. This can help filter shaky data as a
                // finger is lifted.
                if (currPressure / prevPressure > PRESSURE_THRESHOLD) {
                    final boolean updatePrevious = listener.onMove(this);//目前onMove只回傳false,也就是沒用上的意思
                    if (updatePrevious) {
                        prevEvent.recycle();
                        prevEvent = MotionEvent.obtain(event);
                    }
                }
                break;
        }
    }

    //如果沒有複寫也可以直接用updateStateByEvent(curr);就會直貼使用父方法
    @Override
    protected void updateStateByEvent(MotionEvent curr) {
        super.updateStateByEvent(curr);//這邊先執行一次父類別方法

        //下面是要改寫或擴增的部分
        MotionEvent prev = prevEvent;

        // Focus intenal
        currFocusInternal = determineFocalPoint(curr);//手指頭之間的中心點
        prevFocusInternal = determineFocalPoint(prev);//手指頭之間的中心點

        // Focus external
        // - Prevent skipping of focus delta when a finger is added or removed
        boolean skipNextMoveEvent = prev.getPointerCount() != curr.getPointerCount();//如果PointerCount不一樣代表增加或減少了手指的數量,沒增減通常得到false
        //前後手指頭數量不一樣就會得到true就代表忽略, 使用FOCUS_DELTA_ZERO也就是指頭有變化的那次就忽略delta,否則就每次計算前後位置
        focusDeltaExternal = skipNextMoveEvent ?
                FOCUS_DELTA_ZERO : new PointF(currFocusInternal.x - prevFocusInternal.x,  currFocusInternal.y - prevFocusInternal.y);

        Config.LOGD("skipNextMoveEvent: " + skipNextMoveEvent);
        Config.LOGD("focusDeltaExternal: " + focusDeltaExternal);

        // - Don't directly use mFocusInternal (or skipping will occur). Add
        // 	 unskipped delta values to mFocusExternal instead.
        focusExternal.x += focusDeltaExternal.x;
        focusExternal.y += focusDeltaExternal.y;
    }

    //決定手指頭之間的中心點
    private PointF determineFocalPoint(MotionEvent e) {
        // Number of fingers on screen
        final int pCount = e.getPointerCount();
        float x = 0f;
        float y = 0f;

        for(int i = 0; i < pCount; i++) {
            x += e.getX(i);//x分量加總
            y += e.getY(i);//y分量加總
        }

        return new PointF(x/pCount, y/pCount);//總量除以個數得到平均的x與y值,也就是幾個手指頭的中心點
    }

    //取得先跟後之間中心點的平移量
    public PointF getFocusDelta() {
        return focusDeltaExternal;
    }
}
