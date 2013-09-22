package system;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.DataFormatException;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FilePoolManager {
	public final static int FILE_TYPE_IMAGE = 1;
	public final static int FILE_TYPE_VIDEO = 2;
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
		String timeStamp = getDateTime();
		File mediaFile = null;
		
		Log.d("ryan","Time:" + timeStamp);
		
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
	
	private final static String getDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.getDefault());
		return sdf.format(new Date());
	}
}
	