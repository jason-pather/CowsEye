package nz.co.android.cowseye.activity;
import nz.co.android.cowseye.R;
import nz.co.android.cowseye.R.id;
import nz.co.android.cowseye.R.layout;
import nz.co.android.cowseye.R.string;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.gps.GPSManager;
import nz.co.android.cowseye.gps.MapManager;
import nz.co.android.cowseye.utility.Utils;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/** 
 * This is the activity for selecting the location of the pollution event
 * @author Mitchell Lane
 *
 */
public class RecordLocationActivity extends MapActivity {

	private Button backButton;
	private Button nextButton;

	private static LocationManager mLocationManager;
	private EditText addressEditText;
	private GPSManager gpsManager;
	private MapManager mapManager;
	private ProgressDialog dialog;
	
	//Only show link and coordinates if
	//1. The App has been able to use location services and;
	//2. The Reverse Geo-Coder has worked and;
	//3. The user hasn't overridden the location with their own text.
	private boolean link = false;
	//address got from reverse geo coding
	private String geoAddress;
	private GeoPoint addressCoordinates;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_layout);
		addressEditText = (EditText)findViewById(R.id.addressEditText);
		Intent intent = getIntent();
		if(intent.hasExtra(Constants.LOCATION_KEY))
			addressEditText.setText(intent.getStringExtra(Constants.LOCATION_KEY));
		backButton = (Button)findViewById(R.id.backButton);
		nextButton = (Button)findViewById(R.id.doneButton);
		backButton.setOnClickListener(new Utils.BackEventOnClickListener(this));
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hasAllDetails()){
					Intent intent = buildLocationDataIntent(RESULT_OK);
					finish();
					startActivity(intent);
					// get coordinates from address location
//					dialog = ProgressDialog.show(LocationActivity.this, "Acquiring coordinates from address", "Please wait...");
					//TODO DO NOT 
//					new GeoCodeCoordinatesService(LocationActivity.this, gpsManager.getGeoCoder(), addressEditText.getText().toString().trim()).execute();
				}
				else
					Toast.makeText(RecordLocationActivity.this, getResources().getString(R.string.pleaseEnterDetails), Toast.LENGTH_SHORT).show();
			}
		});

		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		// Check if GPS enabled
		if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			buildAlertMessageNoGps(savedInstanceState);
		}
		else
			setupManagers(savedInstanceState);

	}

	private void buildAlertMessageNoGps(final Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getResources().getString(R.string.gps_message))
		.setCancelable(false)
		.setPositiveButton(this.getResources().getString(R.string.gps_positive_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				setupManagers(savedInstanceState);
			}
		})
		.setNegativeButton(this.getResources().getString(R.string.gps_negative_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
				setupManagers(savedInstanceState);

			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	protected void setupManagers(Bundle savedInstanceState) {
		mapManager = MapManager.getInstance((MapView) findViewById(R.id.mapview),false, this);
		gpsManager = GPSManager.getInstance(mapManager, mLocationManager, this, savedInstanceState);
	}

	@Override
	protected void onPause() {
		//remove the listener for gps updates
		if(gpsManager!=null)
			gpsManager.removeUpdates();
		super.onPause();
	}
	@Override
	protected void onResume() {
		//add the listener again for gps updates
		if(gpsManager!=null)
			gpsManager.requestUpdateListeners();
		super.onResume();
	}

	/** Save state of app if activity is destroyed */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		if(gpsManager!=null)
			gpsManager.saveStateOnDestroy(savedInstanceState);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void setAddress(String addr, GeoPoint addressCoordinates) {
		addressEditText.setText(addr);
		link = true;
		//address got from reverse geo coding
		geoAddress = addr;
		this.addressCoordinates = addressCoordinates;
		
	}

	public String getAddress() {
		return addressEditText.getText().toString().trim();
	}

	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	public Intent buildLocationDataIntent(int RESULT_TYPE) {
		if(dialog!=null)
			dialog.dismiss();
		Intent intent=new Intent(this, PreviewActivity.class);
		intent.putExtra(Constants.LOCATION_KEY, getLocation());
		//if reverse geo coded the address
		if(link){
			//if user has not changed the address ( then we can make a link and coordinates)
			if(getLocation().equals(geoAddress)){
				double lat = addressCoordinates.getLatitudeE6(); //addrCoord.getLatitude()*1E6;//gpsManager.getUserLocationGeoPoint().getLatitudeE6() / 1E6;
				double lon = addressCoordinates.getLongitudeE6();//addrCoord.getLongitude()*1E6;//gpsManager.getUserLocationGeoPoint().getLongitudeE6()  / 1E6;
				String link = Constants.GOOGLE_MAP_LINK + (lat/1E6)+","+(lon/1E6);
				intent.putExtra(Constants.LOCATION_LATITUDE_KEY,(int)lat);//gpsManager.getUserLocationGeoPoint().getLatitudeE6());
				intent.putExtra(Constants.LOCATION_LONGITUDE_KEY,(int)lon);// gpsManager.getUserLocationGeoPoint().getLongitudeE6());
				intent.putExtra(Constants.LOCATION_GOOGLE_LINK, link);
				Log.e(toString(), "Putting in link!");
			}
		}
		Log.e(toString(), "Not putting in link :(");
		setResult(RESULT_TYPE, intent);
		Toast.makeText(RecordLocationActivity.this, getResources().getString(R.string.savingAddress), Toast.LENGTH_SHORT).show();
		return intent;
	}
	public void errorGeoCodeAddress(){
		dialog.dismiss();
		Toast.makeText(this, getResources().getString(R.string.errorInGeoCoding), Toast.LENGTH_SHORT).show();

	}

	public String getLocation(){
		return addressEditText.getText().toString().trim();
	}

	protected boolean hasAllDetails() {
		return !getLocation().equals("");
	}

}
