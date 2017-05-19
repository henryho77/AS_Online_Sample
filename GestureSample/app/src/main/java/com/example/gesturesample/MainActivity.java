package com.example.gesturesample;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.gesturesample.app.Config;
import com.example.gesturesample.multitouch.MoveGestureDetector;
import com.example.gesturesample.multitouch.RotateGestureDetector;

public class MainActivity extends Activity {

    private ImageView imageView;

    private Matrix matrix = new Matrix();//matrix用來控制圖片的scale,translate,rotate
    private float scaleFactor = .4f;//縮放改變時會更改的參數
    private float rotationDegrees = 0.f;//旋轉角度改變時會更改的參數
    private float focusX = 0.f;//平移時會更改的x參數
    private float focusY = 0.f;//平移時會更改的y參數
    private int alpha = 255;
    private int imageHeight, imageWidth;

    private MoveGestureDetector moveGestureDetector;
    private RotateGestureDetector rotateGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
                * 圖片與螢幕的關係靠的是左上角的座標點, 所以們需要幾個主要的資訊來控制相關位置
                * 1.螢幕中心點 => (focusX, focusY)
                * 2.圖片的中心點 => 取得圖片長與寬,再分別取1/2距離,到時候扣掉這段距離就會移到中間
                * 3.若有scale, 就要乘上 sacle
                * 4. */
        // 先取得螢幕中心點 Determine the center of the screen to center 'earth'
        Display display = getWindowManager().getDefaultDisplay();
        focusX = display.getWidth() / 2f;
        focusY = display.getHeight() / 2f;

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnTouchListener(onTouchListener);// Set onTouchListener to the ImageView

        // Determine dimensions of 'earth' image
        Drawable d = getResources().getDrawable(R.drawable.earth);
        imageWidth = d.getIntrinsicWidth();//抓圖片現在所呈現的寬度
        imageHeight = d.getIntrinsicHeight();//抓圖片現在所呈現的高度

        // View is scaled and translated by matrix, so scale and translate initially
        float scaledImageCenterX = (imageWidth * scaleFactor) / 2;
        float scaledImageCenterY = (imageHeight * scaleFactor) / 2;

        matrix.reset();
        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postRotate(rotationDegrees);
        matrix.postTranslate(focusX - scaledImageCenterX, focusY - scaledImageCenterY);//讓圖片呈現在中心點,所以要做個平移,中心點位置分別減掉水平跟垂直距離的一半
        imageView.setImageMatrix(matrix);

        moveGestureDetector = new MoveGestureDetector(getApplicationContext(), new MoveListener());
        rotateGestureDetector = new RotateGestureDetector(getApplicationContext(), new RotateListener());
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            moveGestureDetector.onTouchEvent(event);
            rotateGestureDetector.onTouchEvent(event);

            //重新餵值
            float scaledImageCenterX = (imageWidth * scaleFactor) / 2;
            float scaledImageCenterY = (imageHeight * scaleFactor) / 2;

            matrix.reset();
            matrix.postScale(scaleFactor, scaleFactor);
            matrix.postRotate(rotationDegrees);
            matrix.postTranslate(focusX - scaledImageCenterX, focusY - scaledImageCenterY);//讓圖片呈現在中心點,所以要做個平移,中心點位置分別減掉水平跟垂直距離的一半
            imageView.setImageMatrix(matrix);

            return true;
        }
    };

    private class MoveListener implements MoveGestureDetector.OnMoveGestureListener{
        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF delta = detector.getFocusDelta();
            focusX += delta.x;
            focusY += delta.y;
            return true;
        }

        @Override
        public boolean onMoveBegin(MoveGestureDetector detector) {
            return true;
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {
            // Do nothing, overridden implementation may be used
        }
    }

    private class RotateListener implements RotateGestureDetector.OnRotateGestureListener {

        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            rotationDegrees -= detector.getRotationDegreesDelta();
            return true;
        }

        @Override
        public boolean onRotateBegin(RotateGestureDetector detector) {
            return true;
        }

        @Override
        public void onRotateEnd(RotateGestureDetector detector) {
            // Do nothing, overridden implementation may be used
        }
    }
}
