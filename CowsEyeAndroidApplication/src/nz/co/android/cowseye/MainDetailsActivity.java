package nz.co.android.cowseye;

import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.utility.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

/** 
 * This is the main screen of the CowsEye application
 * @author Mitchell Lane
 *
 */
public class MainDetailsActivity extends Activity {

	//TODO move this to UTILS
	public static final String PHOTO_DONE_KEY = "photo_dk";
	public static final String PROBLEM_DONE_DESCRIPTION = "problem_description_k";
	public static final String LOCATION_DONE_KEY = "location_dk";
	public static final String PROBLEM_DONE_KEY = "problem_dk";
	public static final String CONTACT_DONE_KEY = "contact_dk";
	public static final String CAMERA_FILE_KEY = "camera_fk";
	private static final String LOCATION_LINK = "location_link";
	private static final String LOCATION_LAT = "location_lat";
	private static final String LOCATION_LON = "location_lon";
	private static final String LOCATION_ADDRESS ="location_addr";


	/* current details  */
	private Uri cameraFileUri;
	private String details;
	private String firstName;
	private String lastName;
	private String contactEmail;
	private String contactNumber;
	private String googleLink;
	private GeoPoint userLocation;
	private String userAddress;
	
	private Button buttonPhoto;
	private Button buttonLocation;
	private Button buttonPollutionEvent;
	private Button buttonDescription;
	private Button buttonContactDetails;
	private Button buttonSubmit;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_layout);
		setupUI();
	}

	protected boolean networkIsConnected() {
		ConnectivityManager manager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
		Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		return is3g || isWifi;
	}
	
	
	
	//TODO move action listeners to inner classes
	
	/* Sets up all the action listeners for clicking on the different rows in the table */
	private void setupUI(){
		
		buttonPhoto = (Button)findViewById(R.id.button_take_image);
		buttonLocation = (Button)findViewById(R.id.button_select_location);
		buttonPollutionEvent = (Button)findViewById(R.id.button_select_pollution_event);
		buttonDescription = (Button)findViewById(R.id.button_enter_description);
		buttonContactDetails = (Button)findViewById(R.id.button_enter_contact_details);
		buttonSubmit = (Button)findViewById(R.id.button_submit);

		buttonPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				takeImageWithCamera();
			}
		});
		buttonLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// launch location intent
				Intent intent = new Intent(MainDetailsActivity.this, LocationActivity.class);
				// if we have previous details, insert them.
				if(userAddress!=null && !userAddress.equals(""))
					intent.putExtra(Constants.LOCATION_KEY, userAddress);
				startActivityForResult(intent,Constants.REQUEST_CODE_LOCATION);
			}
		});
		buttonDescription.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//launch problem  description intent
				Intent intent = new Intent(MainDetailsActivity.this, DescriptionActivity.class);
				// if we have previous details, insert them.
				if(details!=null && !details.equals(""))
					intent.putExtra(Constants.DESCRIPTION_KEY, details);
				startActivityForResult(intent,Constants.REQUEST_CODE_PROBLEM_DESCRIPTION);
			}
		});
		buttonContactDetails.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//launch contact details intent
				startActivityForResult(new Intent(MainDetailsActivity.this,ContactDetailsActivity.class), Constants.REQUEST_CODE_CONTACT_DETAILS);
			}
		});
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Coming from picture activity
		if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK){
			if (data == null || data.getData() == null) {
				//Picture has been taken, cameraFileUri holds path to photo taken
			}
		}
		// Coming from location activity
		else if(requestCode == Constants.REQUEST_CODE_LOCATION &&  resultCode == Activity.RESULT_OK && data!=null){
			if(data.hasExtra(Constants.LOCATION_KEY))
				userAddress =  data.getStringExtra(Constants.LOCATION_KEY).trim();
			if(data.hasExtra(Constants.LOCATION_GOOGLE_LINK))
				googleLink =  data.getStringExtra(Constants.LOCATION_GOOGLE_LINK).trim();
			int lat = -1;
			int lon = -1;
			if(data.hasExtra(Constants.LOCATION_LATITUDE_KEY))
				lat =  data.getIntExtra(Constants.LOCATION_LATITUDE_KEY,-1);
			if(data.hasExtra(Constants.LOCATION_LONGITUDE_KEY))
				lon =  data.getIntExtra(Constants.LOCATION_LONGITUDE_KEY,-1);

			if(resultCode == Activity.RESULT_OK &&!userAddress.equals("")){
				if(lat!=-1 && lon!=-1 && !googleLink.equals("")){
					userLocation = new GeoPoint(lat, lon);
					Log.e(toString(), "userLocation: "+userLocation + " lat : "+lat);
				}
				else{
					//erase details in case we have some from before
					userLocation = null;
					googleLink=null;
				}
			}
		}
		// Coming from description activity
		else if(requestCode == Constants.REQUEST_CODE_PROBLEM_DESCRIPTION && data!=null){
			if(data.hasExtra(Constants.DESCRIPTION_KEY)){
				details =  data.getStringExtra(Constants.DESCRIPTION_KEY).trim();
			}
		}
		// Coming from contacts
		else if(requestCode == Constants.REQUEST_CODE_CONTACT_DETAILS && data!=null){
			if(resultCode == Activity.RESULT_OK){
				
			}
		}
	}


	protected void takeImageWithCamera() {
		// create Intent to take a picture and return control to the calling application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// create a file to save the image
		cameraFileUri = Utils.getNewCameraFileUri();
		// set the image file name
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri); 
		//trigger activity
		startActivityForResult(intent, 	Constants.REQUEST_CODE_CAMERA);
	}
	
	//TODO move this to UTILS
	protected void buildAlertMessageNoInternet() {
		//Activity transfer to wifi settings

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getResources().getString(R.string.no_internet_message))
		.setCancelable(false)
		.setPositiveButton(this.getResources().getString(R.string.gps_positive_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

			}
		})
		.setNegativeButton(this.getResources().getString(R.string.gps_negative_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}




}