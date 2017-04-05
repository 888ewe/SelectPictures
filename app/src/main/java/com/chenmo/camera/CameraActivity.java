package com.chenmo.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 自定义相机
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private Camera.PictureCallback mPictureCallBack=new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File tempFile=new File("/sdcard/temp.png");
            try {
                FileOutputStream fos=new FileOutputStream(tempFile);
                fos.write(data);
                fos.close();
                Intent intent=new Intent(CameraActivity.this,ResultActivity.class);
                intent.putExtra("picPath",tempFile.getAbsolutePath());
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mPreview = (SurfaceView) findViewById(R.id.surface);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(this);
        //点击屏幕自动对焦
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });
    }


    public void camera(View v) {

        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(800,400);
        //自动对焦
        parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success) {
                    mCamera.takePicture(null,null,mPictureCallBack);
                }
            }
        });

    }

    /**
     * 开始预览相机内容
     */
    private void setstartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            //将系统的横屏变成竖屏
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {
            mCamera = getCamera();

            if (mHolder != null) {
                setstartPreview(mCamera, mHolder);
            }
        }
    }

    /**
     * 获取camera
     *
     * @return
     */
    private Camera getCamera() {

        Camera camera;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setstartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        setstartPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }
}
