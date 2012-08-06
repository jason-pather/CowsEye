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
	
	/** Generates an email encoded as an HTML String and sends an email as intent
	 * @param name name of user
	 * @param userAddress address of user
	 * @param userLocation location as latitude and longitude of user
	 * @param googleLink link to location of user on google maps
	 */
	public static void sendEmail(String cameraFileURI, String description, String googleLink, GeoPoint userLocation, String userAddress, String name, String number, MainDetailsActivity callingActivity){
		new GenerateEmail(cameraFileURI, description, googleLink,userLocation, userAddress, name, number, callingActivity).execute(new String[]{});
	}
	
	private static class GenerateEmail extends AsyncTask<String, Void, String> {

		private String imageName;
		private String description;
		private MainDetailsActivity callingActivity;
		private String googleLink;
		private GeoPoint userLocation;
		private String userAddress;
		private String name;
		private String number;

		public GenerateEmail(String imageName, String description, String googleLink, GeoPoint userLocation, String userAddress, String name, String number, MainDetailsActivity callingActivity){
			this.imageName = imageName;
			this.description = description;
			this.callingActivity = callingActivity;
			this.googleLink = googleLink;
			this.userLocation = userLocation;
			this.userAddress = userAddress;
			this.number = number;
			this.name = name;
		}

		public String doInBackground(String ... messages) {
			String email= generateEmail(imageName, description, googleLink, userLocation,userAddress, number, name);
			return email;
		}

		@Override
		protected void onPostExecute(String emailMessage) {
			callingActivity.sendEmailIntent(emailMessage, imageName);
		}		

	}

	public static String generateEmail(String imageName, String problemDescription, String googleLink, GeoPoint userLocation, String userAddress, String number, String name) {
		return getHeader() +getDescription(problemDescription) + 
				getLocationInGoogleMap(googleLink) + getCoordinates(userLocation) + getLocationAddress(userAddress)
				+ getMyName(name) + getMyContactNumber(number);
	}

	private static String getHeader() {
		return "<p>Hi,</p><p>I have identified the following problem, please see photo and the details attached. Could you please fix it? Thanks!</p>";
	}
	private static String getDescription(String problemDescription) {
		return "<p>"+problemDescription+"</p>";	
	}
	private static String getLocationInGoogleMap(String link) {
		if(link==null || link.equals(""))
			return "";
		return "<p>Location in Google Map: <a href=\""+link+"\" target=\"_blank\">"+link+"</a></p>";
	}
	private static String getCoordinates(GeoPoint location) {
		if(location==null)
			return "";
		return "<p>Longitude & Latitude Coordinates: "+(location.getLatitudeE6() /1E6) +", "+(location.getLongitudeE6()  /1E6 )+"</p>";
	}
	private static String getLocationAddress(String address) {
		return "<p>Location Address: "+address+", Wellington</p>";
	}
	private static String getMyName(String name) {
		return "<p>My Name: "+name+"</p>";
	}

	private static String getMyContactNumber(String number) {
		return "<p>My Contact Number: "+number+"</p>";
	}

//	private static String getFooterPart() {
//		return "<  <a href=\"http://www.resene.com/ezypaint\" target=\"_blank\">www.resene.com/ezypaint</a>.</p><p><strong>Disclaimer:</strong><p>This colour is a representation only. Please refer to the actual paint or product sample. Resene <a href=\"http://www.resene.com/comn/whtsnew/colrordr/order.htm\" target=\"_blank\" class=\"email-link\" style=\"color:#0065d6; text-decoration:none;\">colour charts</a>, <a href=\"https://secure.resene.co.nz/samples/SelectChart.php?productType=1\" target=\"_blank\" class=\"email-link\" style=\"color:#0065d6; text-decoration:none;\">testpots</a> and <a href=\"https://secure.resene.co.nz/samples/SelectChart.php?productType=3\" target=\"_blank\" class=\"email-link\" style=\"color:#0065d6; text-decoration:none;\">samples</a> are available for ordering online.</p><p style=\"font-size:15px; line-height:19px; font-family:Arial, Helvetica, sans-serif; color:#000000; padding:0; margin-top:0; margin-right:0; margin-bottom:7px; margin-left:0;\">This discount on Resene testpots is not available with any other offer. </p><p style=\"margin-top:25px;font-size:15px;line-height:19px;font-family:Arial, Helvetica, sans-serif;color:#000000;margin-bottom:7px;\"><span class=\"text-emphasis\" style=\"font-size:17px; line-height:21px; color:#333333; display:block;\">Check out the Resene <a href=\"http://www.resene.com/colourmatch.htm\" target=\"_blank\" class=\"email-link\" style=\"color:#0065d6; text-decoration:none;\">ColourMatch App</a> for iPhone courtesy of <a href=\"http://www.resene.com/\" target=\"_blank\" class=\"email-link\" style=\"color:#0065d6; text-decoration:none;\">Resene</a>.</span></p></td></tr><tr><td class=\"email-footer\" align=\"center\" valign=\"top\" style=\"background-color:#000000;\"><a href=\"http://www.resene.com/\" target=\"_blank\"><img src=\"http://clients.chrometoaster.com/resene/html_email/resene-footer-logo.jpg\" alt=\"Resene - the paint the professionals use. \"></a>";
//	}
}