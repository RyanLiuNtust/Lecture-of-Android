package lectureOfCamera;

import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

public class CameraSetting {
	private static int mCurrentSDK = utility.PackageManager.getSupportSDK();
	private List<String> mFocusModeList = null;
	private Camera mCamera = null;
	private String TAG = "CameraSetting";
	
	public CameraSetting(Camera camera) {
		mCamera = camera;
		try {
			mFocusModeList = mCamera.getParameters().getSupportedFocusModes();
		} 
		catch(NullPointerException e){
			Log.d(TAG, "Camera is null point result in error setting");
		} 
		
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