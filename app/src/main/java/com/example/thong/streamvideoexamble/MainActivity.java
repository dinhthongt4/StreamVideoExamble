package com.example.thong.streamvideoexamble;


import android.app.Activity;


import android.hardware.Camera;


import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Camera.PictureCallback mRawCallback;
    private Camera.ShutterCallback mShutterCallback;
    private Camera.PictureCallback mJegCallback;

    @AfterViews
    public void initValues() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mJegCallback = new Camera.PictureCallback() {

            FileOutputStream outputStream;

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    outputStream = new FileOutputStream(String.format("/sdcard/%d.jsp"));
                    outputStream.write(data);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_LONG).show();
                refreshCamera();
            }
        };
    }

    public void captureImage(View v) throws IOException {
        mCamera.takePicture(null, null, mJegCallback);
    }

    public void refreshCamera() {
        if (mSurfaceHolder == null) {
            return;
        }
        mCamera.stopPreview();

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.stopPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(352, 288);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
}

