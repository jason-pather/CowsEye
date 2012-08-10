package nz.co.android.cowseye.utility;

import java.io.File;
import java.util.Date;

import com.google.android.maps.GeoPoint;

import nz.co.android.cowseye.RecordLocationActivity;
import nz.co.android.cowseye.MainScreenActivity;
import nz.co.android.cowseye.SelectImageActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Utils {
	
	//Camera Constants
	public static final File DIR_MEDIA_STORAGE = new File(Environment.getExternalStoragePublicDirectory(
			Environment.DIRECTORY_PICTURES), "RiverWatch");
	public static final String CAMERA_FILE_NAME_PREFIX = "RiverWatch_";

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
	
	/** Goes back in the activity stack
	 *  Simply finishes the current activity */
	public static class BackEventOnClickListener implements OnClickListener{
		private Activity activity;
		
		/**
		 * @param a - Activity to finish upon click
		 */
		public BackEventOnClickListener(Activity a){
			this.activity = a;
		}
		@Override
		public void onClick(View v) {
			//clicking back so set the result to cancelled
			Intent intent=new Intent();
			activity.setResult(Activity.RESULT_CANCELED, intent);
			activity.finish();
		}
	}
	
	/** Finishes the current activity and starts a new intent to a new activity upon click */
	public static class StartNextActivityEventOnClickListener implements OnClickListener{
		private Activity activity;
		private Intent activityToStartIntent;
		
		/**
		 * @param a - Coming from this Activity
		 * @param activityToClass - Class of the activity to start
		 */
		public StartNextActivityEventOnClickListener(Activity a, Class<?> activityToClass){
			activityToStartIntent = constructActivityStartIntent(a, activityToClass);
			this.activity = a;
		}
		
		/**
		 * @param a - Coming from this Activity
		 * @param activityToStartIntent - Intent to start the new activity
		 */
		public StartNextActivityEventOnClickListener(Activity a, Intent activityToStartIntent){
			this.activityToStartIntent = activityToStartIntent;
			this.activity = a;
		}
		@Override
		public void onClick(View v) {
			activity.finish();
			activity.startActivity(activityToStartIntent);
		}	
	}
	
	/**
	 * Helper method for creating an intent to go from one activity to another activity 
	 * @param activityFrom - the activity you are coming from
	 * @param activityToClass - the class of the activity to go to
	 * @return
	 */
	public static Intent constructActivityStartIntent(Activity activityFrom, Class<?> activityToClass){
		return new Intent(activityFrom, activityToClass);
	}
	
}