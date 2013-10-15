package lectureOfCamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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
	private int mBack  = -1;
	private Camera  mCamera  = null;
	private Context mContext = null;
	private boolean mIsFront = false;
	private CameraSetting  mCameraSetting = null;
	private PackageManager mPackageManager = null;
	private static int mCurrentSDK = utility.PackageManager.getSupportSDK();
	private AutoFocusCallback mFocusCallback = new Camera.AutoFocusCallback() {
		// it will start when mode of focus is "focus_mode_auto" or "focus_mode_macro"
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			Log.d("ryan","autofocus is starting....");
		}
	};
	
	public CameraFactory(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
		getCameraInfo();
	}
	
	public void getCameraInstance() {
		try {
			if(mIsFront) {
				Log.d("ryan","Front:" + mFront);
				mCamera = Camera.open(mFront);
			}
			else {
				Log.d("ryan","Back:" + mBack);
				mCamera = Camera.open(mBack);
			}
			
			mCameraSetting = new CameraSetting(mCamera);
		}
		catch (Exception e) {
			Log.d(TAG,"Can not get the camera instance");
			Toast.makeText(mContext, "Can not open camera instance", Toast.LENGTH_SHORT).show();
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
	
	public void changeCameraFacing() {
		Log.d("ryan","IsFront:" + mIsFront);
		
		try {
			if(mCamera == null) {
				if(mIsFront && mBack != -1) {
					Log.d("ryan","change");
					mCamera  = Camera.open(mBack);
				}
				else if(mFront != -1) {
					Log.d("ryan","change to front");
					mCamera  = Camera.open(mFront);
				}
			}
		} catch (NullPointerException e) {
			Log.d("ryan",e.getMessage());
			Log.d("ryan","camera can not open");
		}
		mCameraSetting = null;
		mCameraSetting = new CameraSetting(mCamera);
		
		mIsFront = !mIsFront;
		mCamera.startPreview();
	}
	
	public void initCameraParams(int width, int height) {
		if(mCamera == null) return;
		
		String focusMode = null;
		Camera.Parameters params = mCamera.getParameters();
		
		Camera.Size optimalSize = getOptimalPreviewSize(width, height, params);
		
		params.setPreviewSize(optimalSize.width, optimalSize.height);
		
		Log.d("ryan","setparam IsFront:" + mIsFront);
		
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
	
	public void removeCameraInstance() {
		if(mCamera != null) {
			Log.d("ryan","removeCameraInstance");
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;	
		}
	}
	
	private void getCameraInfo() {
		if(mCurrentSDK >= Build.VERSION_CODES.GINGERBREAD){
			Log.d("ryan", "getCameraInfo");
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			
			for(int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
				
				Camera.getCameraInfo(cameraId, cameraInfo);
				if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					mFront = cameraId;
					Log.d("ryan","front:" + mFront);
				}
				else if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
					mBack = cameraId;
					Log.d("ryan","mBack:" + mBack);
				}
			}
		}
	}
	
	public Camera getCurrentCamera() {
		return mCamera;
	}
	
	public void restartPreview() {
		if(mCamera == null) {
			Log.d("ryan","Camera is null");
			getCameraInstance();
		}
		Log.d("ryan","start preview");
		mCamera.startPreview();
	}
}
