package nz.co.android.cowseye.gps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.activity.RecordLocationActivity;
import nz.co.android.cowseye.service.ReverseGeoCodeCoordinatesService;
import nz.co.android.cowseye.utility.AlertBuilder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

/** Managers the GPS updates and location of client 
 *
 * @author Mitchell Lane
 */
public class GPSManager implements LocationListener{

	AlertDialog alert;

	private static final String USER_LOCATION_LATITUDE_KEY = "LAT_KEY";
	private static final String USER_LOCATION_LONGITUDE_KEY = "LON_KEY";

	private GeoPoint userLocationGeoPoint;
	private Location lastKnownLocation;
	private static MapManager mapHelper;

	private static Context context;
	private static RecordLocationActivity locationActivity;

	private LocationManager locationManager; 
	private Geocoder geocoder;
	/* Singleton*/
	private static GPSManager gpsManager;


	public static GPSManager getInstance(){
		return gpsManager;
	}
	public static GPSManager getInstance(MapManager mapHelper, LocationManager lm, Context app, Bundle savedInstanceState){
		gpsManager = new GPSManager(mapHelper, lm, app, savedInstanceState);
		return gpsManager;
	}

	private GPSManager(MapManager mapHelper, LocationManager lm, Context app, Bundle savedInstanceState){
		this.mapHelper = mapHelper;
		locationManager = lm;
		context = app;
		locationActivity = (RecordLocationActivity)app;
		geocoder = new Geocoder(context, Locale.getDefault());
		setupGPS(savedInstanceState);
	}

	/* Sets up the GPS with receiving updates via GPS and networks */
	private void setupGPS(Bundle savedInstanceState){
		requestUpdateListeners();

		//If no location found in state
		if(!retrieveLocationOnStart(savedInstanceState)){
			//Tries to draw the user position and centre map to it
			try{
				Location loc = determineLastKnownLocation();
				if(loc!=null){
					updateLocationActivity(loc);
//					mapHelper.drawUserPosition(userLocationGeoPoint);
//					mapHelper.setMapViewToLocation(userLocationGeoPoint);
				}
			}catch(NoLocationFoundException e){
//				Toast.makeText(context, context.getResources().getString(R.string.gps_no_location_message), Toast.LENGTH_SHORT).show();
				Log.e(toString(), "Cannot get a fix on user location: "+e.toString());
			}
		}
		//location found so draw it
		else{
			mapHelper.drawUserPosition(userLocationGeoPoint);
		}
	}

	/* Returns the last known location of the current user as a geo point, containing the latitude and longitude */
	private Location determineLastKnownLocation() throws NoLocationFoundException{
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
//		Log.d(toString(), "locMan: "+ locationManager);
//		Log.d(toString(), "provid: "+ locationManager.getBestProvider(criteria, true));
		
		String provider = locationManager.getBestProvider(criteria, true);
		if(provider==null)
			throw new NoLocationFoundException("No provider - LocationManager");
		Location location = locationManager.getLastKnownLocation(provider);
		if(location==null){
			throw new NoLocationFoundException("Originated from FriendFinderActivity.determineUserPosition - LocationManager");
		}
		lastKnownLocation = location;
		userLocationGeoPoint = new GeoPoint((int)(lastKnownLocation.getLatitude()*1E6), (int)(lastKnownLocation.getLongitude()*1E6));
		return lastKnownLocation;
	}

	/** removes the gps update listeners*/
	public void removeUpdates() {
		locationManager.removeUpdates(this);
	}

	/** Adds the gps update listeneres */
	public void requestUpdateListeners() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  12000, 10, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 12000, 10, this);
	}

	/** Returns the location of the user as a geo point containing latitude and longitutude */
	public GeoPoint getUserLocationGeoPoint() {
		return userLocationGeoPoint;
	}

	/**
	 * 	Saves the last location upon destroying of main activity
	 */
	public void saveStateOnDestroy(Bundle savedInstanceState) {
		//store user location
		if(userLocationGeoPoint!=null){
			savedInstanceState.putInt(USER_LOCATION_LATITUDE_KEY, userLocationGeoPoint.getLatitudeE6());
			savedInstanceState.putInt(USER_LOCATION_LONGITUDE_KEY, userLocationGeoPoint.getLongitudeE6());
		}
	}	

	/**
	 * Set last location upon resume
	 * @return true if the location retrieved is successfull, otherwise false
	 */
	public boolean retrieveLocationOnStart(Bundle savedInstanceState) {
		if(savedInstanceState==null)
			return false;
		//Retrieve user location if it exists
		if(savedInstanceState.containsKey(USER_LOCATION_LATITUDE_KEY) && savedInstanceState.containsKey(USER_LOCATION_LONGITUDE_KEY)){
			userLocationGeoPoint = new GeoPoint(savedInstanceState.getInt(USER_LOCATION_LATITUDE_KEY),savedInstanceState.getInt(USER_LOCATION_LONGITUDE_KEY));
			return true;
		}
		return false;
	}

	/** Received a location change event from GPS or network provider */
	@Override
	public void onLocationChanged(Location location) {
		// update last known position
		lastKnownLocation = location;
		userLocationGeoPoint =new GeoPoint((int) ( location.getLatitude() * 1E6), (int) ( location.getLongitude() * 1E6));
//		locationActivity.setGeoCoordinates(userLocationGeoPoint);
		// add new user position
		updateLocationActivity(location);
		Log.i(toString(), "User location changed : "+(int) ( location.getLatitude() * 1E6)+" , "+ (int) ( location.getLongitude() * 1E6));
	}

	/** Converts a location with latitude and longitude coordinates to an address */
	public void updateLocationActivity(Location location) {
		//execute service to get a human readable address from given latitude and longitude coordinates
		new ReverseGeoCodeCoordinatesService(context, this, geocoder, location, locationActivity.getAddress().trim(),userLocationGeoPoint).execute();
	}
	/** Converts a given address in text to latitude and longitude coordinates in an Address object */
	public Address getCoordinatesFromAddress(String addr){
		try{
			List<Address> addresses = geocoder.getFromLocationName(addr,1);
			if (addresses == null) {
				Log.e(toString(), "No lat,long found from addr :"+addr);
				return null;
			}
			Address location = addresses.get(0);
			location.getLatitude();
			location.getLongitude();
			return location;

		} catch (IOException e) {
			Log.e(toString(), "Geocoding error: "+e);
			Toast.makeText(context, context.getResources().getString(R.string.errorInGeoCoding), Toast.LENGTH_SHORT).show();
		}
		return null;
	}
	@Override
	public void onProviderDisabled(String provider) {}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	public void requestBuildAlertMessageUpdatePosition(String addr, GeoPoint userPoint) {
		if(alert!=null)
			alert.dismiss();
		AlertDialog alert = AlertBuilder.buildAlertMessageUpdatePosition(locationActivity, mapHelper, locationActivity, addr, userPoint);
		try{
		if(alert!=null)
			alert.show();
		}
		catch(BadTokenException e){};
	}
	public Geocoder getGeoCoder() {
		return geocoder;
	}
	public void errorReverseGeoCoding() {
		if(alert!=null)
			alert.dismiss();
		Toast.makeText(context, context.getResources().getString(R.string.gps_no_location_message), Toast.LENGTH_SHORT).show();
		Log.e(toString(), "Cannot get a fix on user location: ");
	}


}
