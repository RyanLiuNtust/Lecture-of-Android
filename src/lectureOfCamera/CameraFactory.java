package lectureOfCamera;

import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraFactory {
	
	private static final String TAG = "CameraFactory";
	private Camera mCamera = null;
	private SurfaceHolder mHolder = null;
	//if using thread to run it, it need to be asynchronous thread 
	//to avoid interrupt the main thread operation
	//there are specific cameras could be accessed by using Camera.open(int) method
	public void getCameraInstance() {
		try {
			mCamera = Camera.open();
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
			Log.d(TAG,"Can not get the camera instance");
		}
	}

	public void restartPreview() {
		if(mCamera == null) {
			getCameraInstance();
		}
		mCamera.startPreview();
	}
	
	public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
		if(mCamera != null) {
			mCamera.takePicture(shutter, raw, jpeg);
		}
	}
	
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		public CameraPreview(Context context) {
			super(context);
			//to get notified when the underlying surface is create or destroyed
			mHolder = getHolder();
			mHolder.addCallback(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			//If your preview can change or rotate,take care of those events here. 
			if(mHolder.getSurface() == null) return;
			
			//stop preview before make changes
			mCamera.stopPreview();
			
			//set preview size and make any resize, rotate or reformatting changes here
			//......
			
			//start preview with new setting
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			} catch (IOException e) {
				Log.d(TAG, "error starting camera preview: " + e.getMessage());
			}
			
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if(mCamera != null) {
					mCamera.setPreviewDisplay(holder);
					mCamera.startPreview();
				}
			} catch (IOException e) {
				Log.d(TAG, e.getMessage());
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG,"surfaceDestoryed");
			
			this.getHolder().removeCallback(this);
			if(mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		}
	}
}
