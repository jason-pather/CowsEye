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
 * This is the main screen where the user must complete all tasks before submitting an email
 * @author Mitchell Lane
 *
 */
public class MainDetailsActivity extends Activity {

	private static final String EMAIL_ADDRESS_TO = "TextFixIt@wcc.govt.nz";//"info@wcc.govt.nz";
	private static final String EMAIL_SUBJECT = "Fix It Request";

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


	private Drawable topRowSelected; 
	private Drawable topRowUnSelected; 
	private Drawable middleRowSelected; 
	private Drawable middleRowUnSelected; 
	private Drawable bottomRowSelected; 
	private Drawable bottomRowUnSelected; 
	private Drawable submitButtonUnSelected;
	private Drawable submitButtonSelected;


	private TableRow tableRowPhoto;
	private TableRow tableRowLocation;
	private TableRow tableRowProblem;
	private TableRow tableRowContact;

	private TextView textViewPhoto;
	private TextView textViewLocation;
	private TextView textViewProblem;
	private TextView textViewContact;

	private Button submitButton;

	private boolean photoDone = false;
	private boolean locationDone = false;
	private boolean problemDone = false;
	private boolean contactDone = false;

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


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_layout);
		setupTextViews();
		setupTableRowActions();
		submitButton = (Button) findViewById(R.id.detailsSubmitButton);
		Resources res = getResources();

		submitButtonUnSelected = res.getDrawable(R.drawable.submit_button);
		submitButtonSelected = res.getDrawable(R.drawable.submit_button_selected);

		submitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.setBackgroundDrawable(submitButtonSelected);
				String name = firstName + " " +lastName;
				//Check if we have got everything
				if(gotAllDetailsForEmail()){
					//Check for internet access
					if(networkIsConnected()){
						getContactDetails();
						Utils.sendEmail(cameraFileUri.getPath(), details, googleLink, userLocation, userAddress, name, contactNumber, MainDetailsActivity.this);
					}else{
						setNoPressedButtons();
						buildAlertMessageNoInternet();
					}
				}
				else{
					Toast.makeText(MainDetailsActivity.this, getString(R.string.pleaseCompleteAllSteps), Toast.LENGTH_SHORT).show();
					setNoPressedButtons();
				}
			}
		});
		if(savedInstanceState!=null){
			loadSavedState(savedInstanceState);
		}
		getContactDetails();
		if(!firstName.equals("") && !lastName.equals("")&& !contactEmail.equals("") && !contactNumber.equals(""))
			contactDone = true;
		setPhotoDone(photoDone);
		setProblemDone(problemDone);
		setlocationDone(locationDone);
		setContactDone(contactDone);
		checkAllDone();
	}

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


	protected boolean networkIsConnected() {
		ConnectivityManager manager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
		Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		return is3g || isWifi;
	}

	protected boolean gotAllDetailsForEmail() {
		return photoDone && problemDone && locationDone && contactDone; 
	}

	//save all details done so far
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PHOTO_DONE_KEY, photoDone);
		outState.putBoolean(PROBLEM_DONE_KEY, problemDone);
		if(problemDone)
			outState.putString(PROBLEM_DONE_DESCRIPTION, details);
		outState.putBoolean(LOCATION_DONE_KEY, locationDone);
		if(locationDone){
			outState.putString(LOCATION_LINK, googleLink);
			if(userLocation!=null){
				outState.putInt(LOCATION_LAT, userLocation.getLatitudeE6());
				outState.putInt(LOCATION_LON, userLocation.getLongitudeE6());
			}
			outState.putString(LOCATION_ADDRESS, userAddress);
		}
		outState.putBoolean(CONTACT_DONE_KEY, contactDone);
		if(cameraFileUri!=null)
			outState.putString(CAMERA_FILE_KEY, cameraFileUri.getPath());
	}


	//Load state of details
	private void loadSavedState(Bundle inState){
		photoDone = inState.getBoolean(PHOTO_DONE_KEY);
		problemDone = inState.getBoolean(PROBLEM_DONE_KEY);
		if(problemDone)
			details = inState.getString(PROBLEM_DONE_DESCRIPTION);
		locationDone = inState.getBoolean(LOCATION_DONE_KEY);
		if(locationDone){
			userAddress = inState.getString(LOCATION_ADDRESS);
			if(inState.containsKey(LOCATION_LINK)){
				googleLink = inState.getString(LOCATION_LINK);
				int lat = inState.getInt(LOCATION_LAT);
				int lon = inState.getInt(LOCATION_LON);
				userLocation = new GeoPoint(lat, lon);

			}
		}
		contactDone = inState.getBoolean(CONTACT_DONE_KEY);
		if(inState.containsKey(CAMERA_FILE_KEY))
			cameraFileUri = Uri.parse((String) inState.get(CAMERA_FILE_KEY));
	}




	/* Gets all the text views */
	private void setupTextViews(){
		textViewPhoto = (TextView) findViewById(R.id.photoAddedTextView);
		textViewLocation = (TextView) findViewById(R.id.locationAddedTextView);
		textViewProblem = (TextView) findViewById(R.id.problemAddedTextView);
		textViewContact = (TextView) findViewById(R.id.contactAddedTextView);
	}
	/* Sets up all the action listeners for clicking on the different rows in the table */
	private void setupTableRowActions(){
		Resources res = getResources();

		tableRowPhoto = (TableRow) findViewById(R.id.details_photo_row);
		tableRowLocation = (TableRow) findViewById(R.id.details_location_row);
		tableRowProblem = (TableRow) findViewById(R.id.details_problem_row);
		tableRowContact = (TableRow) findViewById(R.id.details_contact_row);

		topRowSelected = res.getDrawable(R.drawable.top_table_row_border_selected);
		middleRowSelected = res.getDrawable(R.drawable.middle_table_row_border_selected);
		bottomRowSelected = res.getDrawable(R.drawable.bottom_table_row_border_selected);
		topRowUnSelected = res.getDrawable(R.drawable.top_table_row_border);
		middleRowUnSelected = res.getDrawable(R.drawable.middle_table_row_border);
		bottomRowUnSelected = res.getDrawable(R.drawable.bottom_table_row_border);

		tableRowPhoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.setBackgroundDrawable(topRowSelected);
				takeImageWithCamera();
			}
		});
		tableRowLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.setBackgroundDrawable(middleRowSelected);
				// launch location intent
				Intent intent = new Intent(MainDetailsActivity.this, LocationActivity.class);
				// if we have previous details, insert them.
				if(userAddress!=null && !userAddress.equals(""))
					intent.putExtra(Constants.LOCATION_KEY, userAddress);
				startActivityForResult(intent,Constants.REQUEST_CODE_LOCATION);
			}
		});
		tableRowProblem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.setBackgroundDrawable(middleRowSelected);
				//launch problem  description intent
				Intent intent = new Intent(MainDetailsActivity.this, DescriptionActivity.class);
				// if we have previous details, insert them.
				if(details!=null && !details.equals(""))
					intent.putExtra(Constants.DESCRIPTION_KEY, details);
				startActivityForResult(intent,Constants.REQUEST_CODE_PROBLEM_DESCRIPTION);
			}
		});
		tableRowContact.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				v.setBackgroundDrawable(bottomRowSelected);
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
				setPhotoDone(true);	
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
				setlocationDone(true);
			}
		}
		// Coming from description activity
		else if(requestCode == Constants.REQUEST_CODE_PROBLEM_DESCRIPTION && data!=null){
			if(data.hasExtra(Constants.DESCRIPTION_KEY)){
				details =  data.getStringExtra(Constants.DESCRIPTION_KEY).trim();
				setProblemDone(!details.equals(""));
			}
		}
		// Coming from contacts
		else if(requestCode == Constants.REQUEST_CODE_CONTACT_DETAILS && data!=null){
			if(resultCode == Activity.RESULT_OK){
				setContactDone(true);
				getContactDetails();
			}
		}
		setNoPressedButtons();
	}


	/* Open up the email activity and fill it with the details */
	public void sendEmailIntent(String htmlString, String imageName) {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/html");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,EMAIL_SUBJECT);
		sharingIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{EMAIL_ADDRESS_TO});
		//		String img = "<img src=\"my_image\">";
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(htmlString));
		sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ imageName));
		startActivity(Intent.createChooser(sharingIntent,"Send using"));
		submitButton.setBackgroundDrawable(submitButtonUnSelected);
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

	/* Changes all the row drawables to unselected */
	private void setNoPressedButtons() {
		tableRowPhoto.setBackgroundDrawable(topRowUnSelected);
		tableRowLocation.setBackgroundDrawable(middleRowUnSelected);
		tableRowProblem.setBackgroundDrawable(middleRowUnSelected);
		tableRowContact.setBackgroundDrawable(bottomRowUnSelected);
		submitButton.setBackgroundDrawable(submitButtonUnSelected);
	}

	private void getContactDetails(){
		SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
		firstName = prefs.getString(Constants.SHARED_PREFS_FIRST_NAME, "");
		lastName = prefs.getString(Constants.SHARED_PREFS_LAST_NAME, "");
		contactEmail = prefs.getString(Constants.SHARED_PREFS_EMAIL, "");
		contactNumber = prefs.getString(Constants.SHARED_PREFS_NUMBER, "");
	}

	/*--------------------- Controls if all details are entered ----------------------------*/

	private void setPhotoDone(boolean b){
		photoDone = b;
		textViewPhoto.setTextColor(b? getResources().getColor(R.color.detail_added_green) : getResources().getColor(R.color.details_background_gray));
		textViewPhoto.setText(b? getResources().getString(R.string.photoAdded): getResources().getString(R.string.noPhotoAdded));
		checkAllDone();
	}


	private void setlocationDone(boolean b){
		locationDone = b;
		textViewLocation.setTextColor(b? getResources().getColor(R.color.detail_added_green) : getResources().getColor(R.color.details_background_gray));
		textViewLocation.setText(b? getResources().getString(R.string.locationRecorded): getResources().getString(R.string.noLocationRecorded));
		checkAllDone();
	}
	private void setProblemDone(boolean b){
		problemDone = b;
		textViewProblem.setTextColor(b? getResources().getColor(R.color.detail_added_green) : getResources().getColor(R.color.details_background_gray));
		textViewProblem.setText(b? getResources().getString(R.string.descriptionAdded): getResources().getString(R.string.noDescriptionAdded));
		checkAllDone();
	}
	private void setContactDone(boolean b){
		contactDone = b;
		textViewContact.setTextColor(b? getResources().getColor(R.color.detail_added_green) : getResources().getColor(R.color.details_background_gray));
		textViewContact.setText(b? getResources().getString(R.string.contactDetailsAdded): getResources().getString(R.string.noContactDetailsAdded));
		checkAllDone();
	}
	private void checkAllDone() {
		if(gotAllDetailsForEmail())
			submitButton.setTextColor( getResources().getColor(R.color.detail_added_green));
		else
			submitButton.setTextColor( getResources().getColor(R.color.details_background_gray));

	}

	@Override
	protected void onResume() {
		super.onResume();
	}



}