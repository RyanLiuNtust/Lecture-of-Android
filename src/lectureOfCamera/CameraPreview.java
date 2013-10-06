package lectureOfCamera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private String TAG = "CameraPreview";
	private Camera mCamera = null;
	private SurfaceHolder mHolder = null;
	private CameraFactory mCameraFactory = null;
	
	public CameraPreview(Context context, CameraFactory cameraFactory) {
		super(context);
		//to get notified when the underlying surface is create or destroyed
		Log.d(TAG,"get Holder");
		mHolder = getHolder();
		mHolder.addCallback(this);
		mCameraFactory = cameraFactory;
		mCamera = mCameraFactory.getCurrentCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		//If your preview can change or rotate,take care of those events here.
		Log.d(TAG,"surfaceChanged");
		if(mHolder.getSurface() == null || mCamera == null) {
			Log.d(TAG, "Camera does not open or SurfaceHolder is null");
			return;
		}
		
		//stop preview before make changes
		mCamera.stopPreview();
		
		//set preview size and make any resize, rotate or reformatting changes here
		//......
		mCameraFactory.initCameraParams(width,height);
		
		//start preview with new setting
		try {
			Log.d("ryan", "setpreviewdisplay");
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d(TAG, "error starting camera preview: " + e.getMessage());
		}
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG,"surfaceDestoryed");
		
		removeHolderCallback();
		mCameraFactory.removeCameraInstance();
	}
	
	public void removeHolderCallback() {
		Log.d(TAG,"removeHolderCallback");
		this.getHolder().removeCallback(this);
		mHolder = null;
	}
	
	public SurfaceHolder getSurfaceHolder() {
		return mHolder;
	}
}
