package nz.co.android.cowseye.service;

import java.io.IOException;
import java.util.List;

import nz.co.android.cowseye.gps.GPSManager;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class ReverseGeoCodeCoordinatesService extends AsyncTask<Void, Void, String> {

	private Context context;
	private GPSManager gpsManager;
	private Geocoder geocoder;
	private Location location;
	private String currentAddress;
	private GeoPoint oldGeoPoint;

	public ReverseGeoCodeCoordinatesService(Context context, GPSManager gpsManager, Geocoder geocoder, Location location, String currentAddress, GeoPoint oldGeoPoint){
		this.context = context;
		this.gpsManager = gpsManager;
		this.geocoder = geocoder;
		this.location = location;
		this.currentAddress = currentAddress;
		this.oldGeoPoint = oldGeoPoint;
	}

	protected String doInBackground(Void... Void) {
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			String num = addresses.get(0).getFeatureName().trim();
			String street = addresses.get(0).getThoroughfare().trim();
			String subArea = addresses.get(0).getSubAdminArea().trim();
			String addr =""; 
			if(!num.equals(""))
				addr+=num+=" ";
			if(!street.equals(""))
				addr+=street+", ";
			if(!subArea.equals(""))
				addr+=subArea;
			return addr;

		} catch (IOException e) {
			Log.e(toString(), "Reverse Geocoding error: "+e);
		}
		return null;
	}

	/** Does not do anything as nothing needs to be done upon ending*/
	protected void onPostExecute(String addr) {
		if(addr==null){
			Log.e(toString(), "Error in reverse geo coding");
			gpsManager.errorReverseGeoCoding();
		}
		else if(!addr.equals("")){
			if(!(addr.trim()).equals(currentAddress)){
				gpsManager.requestBuildAlertMessageUpdatePosition(addr,oldGeoPoint);
			}
		}
	}
}