package system;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FilePoolManager {
	public final static int FILE_TYPE_IMAGE = 1;
	public final static int FILE_TYPE_VIDEO  = 2;
	private final static String TAG = "FilePoolManager";
	
	public static Uri getOutputMediaUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}
	
	public static File getOutputMediaFile(int type) {
		File storageDir = new File(Environment.getExternalStoragePublicDirectory(
									Environment.DIRECTORY_PICTURES),"CameraLecture");
		if(!storageDir.exists()) {
			if(!storageDir.mkdirs()) {
				Log.d(TAG,"FAIL TO CREATE DIRECTORY");
				return null;
			}
		}
		//Create file name with time
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile = null;
		if(type == FILE_TYPE_IMAGE) {
			mediaFile = new File(storageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		}
		
		else if (type == FILE_TYPE_VIDEO) {
			mediaFile = new File(storageDir.getPath() + File.separator + "VIDEO_" + timeStamp + ".mp4");
		}
		
		else {
			return null;
		}
		
		return mediaFile;
	}
}
	