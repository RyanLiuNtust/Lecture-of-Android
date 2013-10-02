package utility;

public class PackageManager {
	public static int getSupportSDK() {
		//android.os.Build.VERSION.SDK is deprecate
		//android.os.Build.VERSION.SDK_INT is available if sdk > 3
		return android.os.Build.VERSION.SDK_INT;
	}
}
