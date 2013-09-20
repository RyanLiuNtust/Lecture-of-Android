package lectureOfCamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import system.FilePoolManager;

import com.example.lectureofandroid.R;

import lectureOfCamera.CameraFactory;
import lectureOfCamera.CameraFactory.CameraPreview;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
   
public class CameraActivity extends Activity {
	
	private static final String TAG = "CameraActivity";
	private Camera mCamera = null;
	private PictureCallback mPicture = null;
	private CameraPreview mCameraPreview = null;
	private CameraFactory mCameraFactory = new CameraFactory();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);
		
		//get an instance of Camera
		mCamera = mCameraFactory.getCameraInstance();
		
		//initialize the preview 
		mCameraPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
		preview.addView(mCameraPreview);
		
		//set the data type in jpg and save in specific file directory
		mPicture = getPictureCallback();
		
		Button captureButton = (Button) findViewById(R.id.camera_capture);
		
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCamera.takePicture(null, null, mPicture);
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		uninitalizeCamera();
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		
		if(mCamera == null) {
			mCamera = mCameraFactory.getCameraInstance();
		}
	}
	
	private void uninitalizeCamera() {
		if(mCamera != null) { 
			mCamera.release();
		}
		
		mCamera = null;
	}
	
	private PictureCallback getPictureCallback() {
		return new PictureCallback() {	
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				
				File pictureFile = FilePoolManager.getOutputMediaFile(FilePoolManager.FILE_TYPE_IMAGE);
				if(pictureFile == null) {
					Log.d(TAG,"ERROR CREATING FILE, PLS CHECK THE STORAGE PERMISSION");			
					return ;
				}
				
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					Log.d("ryan","successfully save");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		 };
	}
}
