package utility;
/*sdk version pls refet to 
 * http://stackoverflow.com/questions/3993924/get-android-api-level-of-phone-currently-running-my-application
 */
public class PackageManager {
	public static int getSupportSDK() {
		//android.os.Build.VERSION.SDK is deprecate
		//android.os.Build.VERSION.SDK_INT is available if sdk > 3
		return android.os.Build.VERSION.SDK_INT;
	}
}
