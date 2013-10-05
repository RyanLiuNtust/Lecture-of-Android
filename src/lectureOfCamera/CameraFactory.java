package lectureOfCamera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*If you want to using camera on your application
 * please note that there are something need to add in your manifest
 * ----------------------------------------------
 * Add in the "permission" 
 * 1.android.permission.CAMERA
 * 2.android.permission.WRITE_EXTERNAL_STORAGE
 * ----------------------------------------------
 * Add in the "feature"
 * 1.android.hardware.camera
 * 2.android.hardware.camera.autofocus //optional
 * */

public class CameraFactory {
	//if using thread to run it, it need to be asynchronous thread 
	//to avoid interrupt the main thread operation
	//there are specific cameras could be accessed by using Camera.open(int) method
	//Assume the all user's cell phone has front and back camera.......
	private static final String TAG = "CameraFactory";
	private int mFront = -1;
	private int mBack = -1;
	private boolean mIsFront;
	private Camera mCamera = null;
	private Context mContext = null;
	private CameraSetting mCameraSetting = new CameraSetting();
	private PackageManager mPackageManager = null;
	private AutoFocusCallback mFocusCallback = new Camera.AutoFocusCallback() {
		// it will start when mode of focus is "focus_mode_auto" or "focus_mode_macro"
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			Log.d("ryan","focus....");
		}
	};
	
	public CameraFactory(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
	}
	
	public void getCameraInstance() {
		try {
			if(mCameraSetting.getCurrentSdk() >= Build.VERSION_CODES.GINGERBREAD){
				Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
				for(int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
					Camera.getCameraInfo(cameraId, cameraInfo);
					if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) mFront = cameraId;
					else if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) mBack = cameraId;
				}
			}
			/////////////
			mCamera = Camera.open(mBack);
			mIsFront = false;
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
			Log.d(TAG,"Can not get the camera instance");
		}
	}
	
	public void takePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw, final Camera.PictureCallback jpeg) {
		if(mCamera != null) {
			Log.d("ryan", "take a picture");
			if(mIsFront == false && mPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
				mCamera.autoFocus(mFocusCallback);
			}
			mCamera.takePicture(shutter, raw, jpeg);
		}
	}
	
	public void changeCamera() {
		removeCameraInstance();
		Log.d("ryan","IsFront:" + mIsFront);
		if(mIsFront && mBack != -1) {
			Log.d("ryan","change");
			mCamera  = Camera.open(mBack);
			mIsFront = false;
		}
		else if(mFront != -1){
			Log.d("ryan","change to front");
			mCamera  = Camera.open(mFront);
			mIsFront = true;
		}
	}
	
	private void initCameraParams(int width, int height) {
		if(mCamera == null) return;
		String focusMode = null;
		Camera.Parameters params = mCamera.getParameters();
		
		Camera.Size optimalSize = getOptimalPreviewSize(width, height, params);
		
		params.setPreviewSize(optimalSize.width, optimalSize.height);
		
		Log.d("ryan","setpara IsFront:" + mIsFront);
		
		if(mIsFront == false && (focusMode = mCameraSetting.getFocusMode()) != null){
			params.setFocusMode(focusMode);
		}
		mCamera.setParameters(params);
	}
	
	private Camera.Size getOptimalPreviewSize(int width, int height, Camera.Parameters params) {
		Camera.Size optimalSize = null;
		
		for(Camera.Size size:params.getSupportedPreviewSizes()) {
			if(size.width <= width && size.height <= height) {
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
	
	private void removeCameraInstance() {
		if(mCamera != null) {
			Log.d("ryan","removeCameraInstance");
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;	
		}
	}
	
	public void restartPreview() {
		if(mCamera == null) {
			getCameraInstance();
		}
		mCamera.startPreview();
	}
	
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder = null;
		
		public CameraPreview(Context context) {
			super(context);
			//to get notified when the underlying surface is create or destroyed
			mHolder = getHolder();
			mHolder.addCallback(this);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
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
				Log.d("ryan", "setpreviewdisplay");
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
			
			removeHolderCallback();
			removeCameraInstance();
		}
		
		public void removeHolderCallback() {
			this.getHolder().removeCallback(this);
		}
	}
	
	public class CameraSetting {
		int mCurrentSDK = utility.PackageManager.getSupportSDK();
		List<String> mFocusModeList = null;
		public CameraSetting() {
			if(mCamera != null) {
				mFocusModeList = mCamera.getParameters().getSupportedFocusModes();
			}
		}
		
		public int getCurrentSdk() {
			return mCurrentSDK;
		}
		
		public String getFocusMode() {
			String focusMode = null;
			if(mCurrentSDK <= android.os.Build.VERSION_CODES.ECLAIR) {
				if(mFocusModeList.size() > 0) {
					if(mFocusModeList.contains(Parameters.FOCUS_MODE_AUTO)){
						Log.d("ryan", "FOCUS_MODE_AUTO");
						focusMode = Parameters.FOCUS_MODE_AUTO;
					}
					else if(mFocusModeList.contains(Parameters.FOCUS_MODE_MACRO)) {
						Log.d("ryan", "FOCUS_MODE_MACRO");
						focusMode = Parameters.FOCUS_MODE_MACRO;
					}
				}
			}
			else {
				Log.d("ryan", "FOCUS_MODE_CONTINUOUS_PICTURE");
				focusMode = Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
			}
			return focusMode;
		}
	}
}
