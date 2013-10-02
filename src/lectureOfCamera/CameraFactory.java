package lectureOfCamera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraFactory {
	
	private static final String TAG = "CameraFactory";
	private Camera mCamera = null;
	private SurfaceHolder mHolder = null;
	private CameraSetting cameraSetting = new CameraSetting();
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
	
	public void takePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw, final Camera.PictureCallback jpeg) {
		if(mCamera != null) {
			mCamera.autoFocus(new Camera.AutoFocusCallback() {
				
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					Log.d("ryan","focus....");
				}
			});
			Log.d("ryan", "take a picture");
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
			Log.d(TAG,"surfaceChanged");
			if(mHolder.getSurface() == null) return;
			
			//stop preview before make changes
			mCamera.stopPreview();
			
			//set preview size and make any resize, rotate or reformatting changes here
			//......
			initCameraParams(width,height);
			
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
			Log.d(TAG,"surfaceCreated");
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
		
		private void initCameraParams(int width, int height) {
			if(mCamera == null) return;
			
			Camera.Parameters params = mCamera.getParameters();
			
			Camera.Size optimalSize = getOptimalPreviewSize(width, height, params);
			
			params.setPreviewSize(optimalSize.width, optimalSize.height);
			params.setFocusMode(cameraSetting.getFocusMode());
			mCamera.setParameters(params);
		}
		
		private Camera.Size getOptimalPreviewSize(int width, int height, Camera.Parameters params) {
			Camera.Size optimalSize = null;
			
			for(Camera.Size size:params.getSupportedPreviewSizes()) {
				if(size.width<=width && size.height<=height) {
					if(optimalSize == null) {
						optimalSize = size;
					}
					else {
						int optimalArea = optimalSize.width * optimalSize.height;
						int newArea = size.width * size.height;
						
						if(newArea > optimalArea) {
							optimalArea = newArea;
						}
					}
				}
			}
			return optimalSize;
		}
	}
	
	class CameraSetting {
		int mCurrentSDK = utility.PackageManager.getSupportSDK();
		
		public String getFocusMode() {
			String focusMode = null;
			if(mCurrentSDK <= android.os.Build.VERSION_CODES.ECLAIR) {
				 Log.d("ryan", "focus_mode_auto");
				focusMode = Parameters.FOCUS_MODE_AUTO;
			}
			else {
				Log.d("ryan", "focus_mode_continuous");
				focusMode = Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
			}
			return focusMode;
		}
	}
}
