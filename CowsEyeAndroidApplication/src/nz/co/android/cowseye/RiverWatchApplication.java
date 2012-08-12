package nz.co.android.cowseye;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import nz.co.android.cowseye.event.EventHandler;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class RiverWatchApplication extends Application  {

	private static final long timerDateAndTimeDelay = 0;
	private static final long timerDateAndTimePeriod = 500; // half a second
	private static final long timerZeroDelay = 0;
	private static final long timerEventsProcessingPeriod = 10000; // 10 seconds
	private static final long timerEventsProcessingLargeDelay = 120000; // 2 minutes

	private static boolean eventProcessingSetup = false;

	public EventHandler eventHandler;
	private Timer updateEventsTimer;

	//Start of application
	@Override
	public void onCreate() {
		super.onCreate();
//		loadDatabase();
		setupApplication();
	}

	private void setupApplication() {
		updateEventsTimer = new Timer();
		eventHandler = new EventHandler(this);
//		List<Event> unProcessdEvents = databaseAdapter.getAllUnProcessedEvents(this);
//		for(Event e : unProcessdEvents)
//			eventHandler.addEvent(e);
	}

	/**
	 * Constructs and loads the database
	 */
//	private void loadDatabase(){		
//		databaseConstructor = new DatabaseConstructor(this);
//		try {
//			databaseConstructor.createDataBase();
//		} catch (IOException ioe) {
//			Log.e(this.toString(),"Unable to create database");
//		}
//		try {
//			databaseConstructor.openDataBase();
//		}catch(SQLException sqle){
//			Log.e(this.toString(),"Unable to open database");
//		}
//		databaseAdapter = new DatabaseAdapter(databaseConstructor);
//	}
	

//	/** Adds this event to the database of events
//	 * 
//	 * @param event - event to add
//	 * @param type - type of the event. one of (check_in, check_out, registration)
//	 * @param employeeId - employeedId of the employee if type is registration, otherwise null
//	 */
//	public void addNewEventToDatabase(Event event, String type, String employeeId) {
//		databaseAdapter.addNewEvent(event, type, employeeId);
//	}
	

//	public DatabaseAdapter getDatabaseAdapter() {
//		return databaseAdapter;
//	}


	public EventHandler getEventHandler(){
		return eventHandler;
	}

	/* Sets up and starts the timer to update events */
	private void startEventProcessingTimer(final long initialDelay){
		eventProcessingSetup = true;
		Log.i(toString(), "Starting Event Processing");
		updateEventsTimer = new Timer();
		/* Updates the event processing */
		TimerTask processEvents = new TimerTask() {
			public void run() {
				Log.i(toString(), "processing events");
				eventHandler.processEvents();
			}
		};
		updateEventsTimer.scheduleAtFixedRate(processEvents, initialDelay, timerEventsProcessingPeriod);
	}

	/** Requests the timer to update event processing if not already */
	public void requestStartEventHandling(){
		if(!eventProcessingSetup){
			eventProcessingSetup = true;
			//starts timer with the normal delay of 0 milliseconds
			startEventProcessingTimer(timerZeroDelay);
		}
	}

	/** Stops the current time and starts a delayed timer
	 * Called when the network is down, for a longer delay between trying again */
	public void requestDelayedEventsTimer(){
		stopTimerEventHandling();
		//start event handling with the large delay
		startEventProcessingTimer(timerEventsProcessingLargeDelay);
	}

	/** Forces the event processing to start*/
	public void forceStartEventHandling(){
		//stops the current timer
		stopTimerEventHandling();
		//starts timer again with delay of zero so instant update
		requestStartEventHandling();
	}

	/** Stops the event processing timer if it is currently running */
	public void stopTimerEventHandling(){
		if(eventProcessingSetup){
			Log.i(toString(),"stopping Timer Event Handling" );
			eventProcessingSetup = false;
			updateEventsTimer.cancel();
		}
	}
	/** Returns whether this device is currently connected to a network */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cm==null)
			return false;
		NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		Boolean is3g = networkInfo==null? false : networkInfo.isConnected();
		networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		Boolean isWifi= networkInfo==null? false : networkInfo.isConnected();
		return is3g || isWifi;
	}

	/** Returns whether this device has GPS enabled */
	public boolean isGPSEnabled(){
		LocationManager mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if(mLocationManager==null)
			return false;
		// Check if GPS enabled
		return mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER );
	}

	
	/** Downloads an employee image thumbnail 
	 * @param url - url to download image thumbnail from
	 * @param e - thumbnail belongs to this employee
	 * @param lastImageThumbPath - path where the last thumbnail for the employee is if it exists
	 * @return the local path of the saved thumbnail
	 */
//	private String downloadEmployeeImageThumbnail(String url, String lastImageThumbPath) {
//		
//		Bitmap image = RestClient.getBitmapThroughGETRequestURL(url);
//		if(image!=null){
//			try{
//				String localImageThumbnail = saveBitmapToDisk(image);
//				//Delete last image if it exists
//				if(lastImageThumbPath!=null && !lastImageThumbPath.equals("")){
//					deleteImage(lastImageThumbPath);
//				}
//				return localImageThumbnail;
//			}
//			catch(IOException f){
//				Log.e(toString(), "Could not save image to disk : "+f);
//			}
//		}
//		return null;
//	}

	/* Saves a bitmap to disk */
	private String saveBitmapToDisk(Bitmap bitmap) throws IOException {
		try{
			final long num = System.currentTimeMillis();
			final String ID = getString(R.string.app_name) +num;
			File dir = this.getDir("", Context.MODE_PRIVATE);
			String pathToDir = dir.getAbsolutePath();
			final String pathName = pathToDir + File.separator+ ID;
			FileOutputStream out = new FileOutputStream(pathName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			return pathName;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		throw new IOException("Could not create file or could not write to created file");
	}

	/** Deletes an image from local storage */
	public void deleteImage(String filePath) {
		File imageFile = new File(filePath);
		//delete image
		if(imageFile.exists())
			imageFile.delete();
	}
}
