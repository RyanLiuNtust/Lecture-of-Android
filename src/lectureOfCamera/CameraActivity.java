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
	private PictureCallback mPicture = null;
	private CameraPreview mCameraPreview = null;
	private CameraFactory mCameraFactory = new CameraFactory();
	private FrameLayout mPreview = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);
		
		//get an instance of Camera
		mCameraFactory.getCameraInstance();
		
		//initialize the preview 
		mCameraPreview = mCameraFactory.new CameraPreview(this);
		
		mPreview = (FrameLayout)findViewById(R.id.camera_preview);
		mPreview.addView(mCameraPreview);
		
		//set the data type in jpg and save in specific file directory
		mPicture = getPictureCallback();
		
		Button captureButton = (Button) findViewById(R.id.camera_capture);
		
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCameraFactory.takePicture(null, null, mPicture);
				mCameraFactory.restartPreview();
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG,"onPause");
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		Log.d(TAG,"onResume");
		mCameraFactory.restartPreview();
		mCameraPreview = mCameraFactory.new CameraPreview(this);
		mPreview.addView(mCameraPreview);
	}
	
	private PictureCallback getPictureCallback() {
		return new PictureCallback() {	
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				
				File pictureFile = FilePoolManager.getOutputMediaFile(FilePoolManager.FILE_TYPE_IMAGE);
				if(pictureFile == null) {
					Log.d(TAG,"Error creating file, pls check the storage permission");			
					return;
				}
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		 };
	}
}
