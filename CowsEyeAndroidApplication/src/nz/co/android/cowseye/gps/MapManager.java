package nz.co.android.cowseye.gps;

import java.util.List;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.gps.ontap.UserOnTap;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/** A class that provides setup and helping functions for controlling the mapview, its controller,
 * and markers for yourself and your friend's positions
 * @author Mitchell Lane
 *
 */
public class MapManager{
	public static final String USER_SATELLITE_KEY = "SAT_KEY";

	private static final int POSITION_ZOOM_LEVEL = 18;
	private static final int MARKER_TEXT_SIZE = 20;

	private static MapView mapView;
	private static MapController mapController;
	private List<Overlay> mapOverlays;
	private MapItemizedOverlay myPositionOverlay;
	private OverlayItem userOverlayItem;
	
	private Drawable myPositionMarker;
	private static Context mainActivityContext;

	private boolean satelliteOn;
	
	/* Singleton*/
	private static MapManager mapManager;
	
	public static MapManager getInstance(){
		return mapManager;
	}
	public static MapManager getInstance(MapView mapView, boolean satelliteOn, Context mainActivityContext){
		mapManager = new MapManager(mapView, satelliteOn, mainActivityContext);
		return mapManager;
	}

	private MapManager(MapView mapView, boolean satelliteOn, Context mainActivityContext) {
		this.mapView = mapView;
		this.satelliteOn = satelliteOn;
		setup(mapView, satelliteOn, mainActivityContext);
	}

	private void setup(MapView mapView, boolean satelliteOn, Context mainActivityContext) {
		mapView.setBuiltInZoomControls(false);
		setSatelliteView(satelliteOn);
		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();
		this.mainActivityContext = mainActivityContext;
		setupMarkerDrawables();
		myPositionOverlay = new MapItemizedOverlay(myPositionMarker, mainActivityContext, MARKER_TEXT_SIZE, new UserOnTap(mainActivityContext));
	}

	
	private void setupMarkerDrawables(){
		// gets the drawables
		myPositionMarker = mainActivityContext.getResources().getDrawable(R.drawable.you_are_here_45x45);
	}
	
	
	/** 
	 * Draws the user at the given geo point location 
	 * @location - location of user
	 * */
	public void drawUserPosition(GeoPoint location) {
		// Remove the last user location marker overlay
		if(myPositionOverlay!=null && mapOverlays.contains(myPositionOverlay)){
			mapOverlays.remove(myPositionOverlay);
			myPositionOverlay.removeOverlay(userOverlayItem);
		} 
		userOverlayItem = new OverlayItem(location, "Location found", "You are here!");
		myPositionOverlay.addOverlay(userOverlayItem);
		mapOverlays.add(myPositionOverlay);
		//redraw the markers
		mapView.invalidate();
	}
	
	
	/** Sets the map view to center itself around the given user location geoPoint */
	public static void setMapViewToLocation(GeoPoint userLocationGeoPoint){
		mapController.setZoom(POSITION_ZOOM_LEVEL);
		mapController.animateTo(userLocationGeoPoint);
	}
	
	/** Sets the satellite view on or off */
	public void setSatelliteView(boolean b){
		mapView.setSatellite(b);
		satelliteOn = b;
	}

	public boolean isSatelliteOn() {
		return satelliteOn;
	}
}
