package nz.co.android.cowseye.utility;

import java.io.File;
import java.util.Date;

import com.google.android.maps.GeoPoint;

import nz.co.android.cowseye.MainDetailsActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class Utils {
	
	//Camera Constants
	public static final File DIR_MEDIA_STORAGE = new File(Environment.getExternalStoragePublicDirectory(
			Environment.DIRECTORY_PICTURES), "FIXiT");
	public static final String CAMERA_FILE_NAME_PREFIX = "FIXIT_";

	/** Create a file Uri for saving an image or video */
	public static Uri getNewCameraFileUri() {
		String filename = CAMERA_FILE_NAME_PREFIX + new Date().getTime() + ".jpg";
		return Uri.fromFile(getOutputMediaFile(filename));
	}


	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(String filename) {
		// TODO: To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		// Create the storage directory if it does not exist
		if (!DIR_MEDIA_STORAGE.exists()){
			if (!DIR_MEDIA_STORAGE.mkdirs()){
				Log.e("DetailsActivity", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		File mediaFile = new File(DIR_MEDIA_STORAGE.getPath() + File.separator + filename);

		return mediaFile;
	}
	
}