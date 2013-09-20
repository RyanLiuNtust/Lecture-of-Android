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
	//cameraHardwareCheck() is runtime check whether it has a camera or not 
	//by hasSystemFeature()
	private boolean cameraHardwareCheck(Context context) {
		//if the device has a camera
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		}
		else {
			return false;
		}
	}
	//if using thread to run it, it need to be asynchronous thread 
	//to avoid interrupt the main thread operation
	//there are specific cameras could be accessed by using Camera.open(int) method
	public Camera getCameraInstance () {
		Camera camera = null;
		try {
			camera = Camera.open();
		}
		catch (Exception e) {
			Log.d(TAG,"Can not get the camera instance");
		}
		return camera;
	}
	
	public static class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		
		private Camera mCamera = null;
		private SurfaceHolder mHolder = null;
		
		public CameraPreview(Context context, Camera camera) {
			super(context);
			mCamera = camera;
			//to get notified when the underlying surface is create or destroyed
			mHolder = getHolder();
			mHolder.addCallback(this);
			//mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
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
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e) {
				Log.d(TAG, e.getMessage());
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			
		}
		
	}
}
