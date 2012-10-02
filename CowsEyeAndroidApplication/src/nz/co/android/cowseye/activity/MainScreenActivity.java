package nz.co.android.cowseye.activity;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.R.id;
import nz.co.android.cowseye.R.layout;
import nz.co.android.cowseye.R.string;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.event.Event;
import nz.co.android.cowseye.event.GetIncidentsEvent;
import nz.co.android.cowseye.event.SubmissionEventBuilder;
import nz.co.android.cowseye.event.SubmissionEventBuilderException;
import nz.co.android.cowseye.service.GetIncidentsAsyncTask;
import nz.co.android.cowseye.utility.AlertBuilder;
import nz.co.android.cowseye.utility.JSONHelper;
import nz.co.android.cowseye.utility.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
public class MainScreenActivity extends Activity {

	private Button buttonSubmit;
	private Button buttonGallery;
	private RiverWatchApplication myApplication;
	
	private ProgressDialog progressDialog;
	
	private String[] imageUrls;
	private String[] thumbUrls;
	private String[] descriptions;
	private boolean loadingGridView = false;
	private boolean haveBaseIncidents = false;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_layout);
		myApplication = (RiverWatchApplication)getApplication();
		setupUI();
		new GetIncidentsAsyncTask(MainScreenActivity.this, new GetIncidentsEvent(myApplication, 0, 9)).execute();
	}

	/** This gets called after a successfull submission event as the activity is already open and
	 * this current opened activity is not destroyed 
	 */
	@Override
	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
	}

	protected boolean networkIsConnected() {
		ConnectivityManager manager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);
		Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
		Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		return is3g || isWifi;
	}

	/* Sets up the UI */
	private void setupUI(){
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getString(R.string.loading_images_title));
		progressDialog.setMessage(getString(R.string.please_wait));
		progressDialog.setCancelable(false);
		
		buttonSubmit = (Button)findViewById(R.id.button_submit);
		buttonSubmit.setOnClickListener(new SubmitPollutionEventOnClickListener());
		buttonGallery = (Button)findViewById(R.id.button_view_gallery);
		buttonGallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog.show();
				//only get new list of incidents if we don't already have them
				if(!haveBaseIncidents){
					new GetIncidentsAsyncTask(MainScreenActivity.this, new GetIncidentsEvent(myApplication, 0, 50)).execute();
					loadingGridView = true;
				}
				else
					loadGridView();
			}
		});

	}
	
	/** Saves incident data received from a JSON object after calling the getIncidents web service */
	public void saveIncidentDataUris(JSONArray data){
		progressDialog.dismiss();
		if(data==null ){
			if(loadingGridView)
				Toast.makeText(this, getString(R.string.failure_load_images_msg), Toast.LENGTH_LONG).show();
		}
		else{
			imageUrls = new String[data.length()];
			thumbUrls = new String[data.length()];
			descriptions = new String[data.length()];
			for(int i = 0; i < data.length(); i++ ){
				try {
					JSONObject incident = data.getJSONObject(i);
					if(incident.has(Constants.JSON_THUMBNAIL_URL_KEY) && incident.has(Constants.JSON_IMAGE_URL_KEY)){
						imageUrls[i] = incident.getString(Constants.JSON_IMAGE_URL_KEY);
						thumbUrls[i] = incident.getString(Constants.JSON_THUMBNAIL_URL_KEY);
						descriptions [i] = incident.getString(Constants.JSON_IMAGE_DESCRIPTION_KEY);
					}
				} catch (JSONException e) {
					Log.e(toString(), "No incident found in JSONObject");
				}
				
			}
			haveBaseIncidents = true;
			if(loadingGridView)
				loadGridView();
		}
	}
	public void loadGridView(){
		loadingGridView = false;
		if(progressDialog!=null)
			progressDialog.dismiss();
		Intent i = new Intent(MainScreenActivity.this, GridIncidentGalleryActivity.class);
		i.putExtra(Constants.GALLERY_IMAGES_ARRAY_KEY, imageUrls);
		i.putExtra(Constants.GALLERY_THUMBNAIL_IMAGES_ARRAY_KEY, thumbUrls);
		i.putExtra(Constants.JSON_IMAGE_DESCRIPTION_KEY,descriptions);
		startActivity(i);
	}

	@Override
	protected void onStart() {
		super.onStart();
//        myApplication.requestStartEventHandling();
		Log.i(toString(), "MainScreen requestStartEventHandling");
	}
	
	@Override
	protected void onDestroy() {
		Log.i(toString(), "MainScreen stopTimerEventHandling");
//		myApplication.stopTimerEventHandling();
		super.onDestroy();
	}

	
	/** Starts a submission of a pollution event */
	public class SubmitPollutionEventOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {

			startActivity(new Intent(MainScreenActivity.this, SelectImageActivity.class));
		}

	}

}