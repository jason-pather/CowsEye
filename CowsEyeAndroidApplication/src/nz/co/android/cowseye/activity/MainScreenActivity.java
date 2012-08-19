package nz.co.android.cowseye.activity;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.R.id;
import nz.co.android.cowseye.R.layout;
import nz.co.android.cowseye.R.string;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.event.Event;
import nz.co.android.cowseye.event.SubmissionEventBuilder;
import nz.co.android.cowseye.event.SubmissionEventBuilderException;
import nz.co.android.cowseye.utility.AlertBuilder;
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
public class MainScreenActivity extends Activity {

	private Button buttonSubmit;
	private RiverWatchApplication myApplication;
	
	private Button buttonTESTPOST;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen_layout);
		myApplication = (RiverWatchApplication)getApplication();
		setupUI();
		Log.d(toString(), "onCreate");
		buttonTESTPOST = (Button)findViewById(R.id.button_test_post);
		buttonTESTPOST.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SubmissionEventBuilder builder = SubmissionEventBuilder.getSubmissionEventBuilder();
				builder.setImagePath(Uri.parse("content://media/external/images/media/45193"))
				.setImageDescription("This is an Image Description")
				.setImageTag("Test image tag")
				.setGeoCoordinates(new GeoPoint(12141414, 493124312))
				.setAddress("2 adventure drive");
				try {
					Event e = builder.build();
					boolean success = e.processForSuccess();

				} catch (SubmissionEventBuilderException e) {
					Log.e(toString(), e.toString());
				}
				
			}
		});
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
		buttonSubmit = (Button)findViewById(R.id.button_submit);
		buttonSubmit.setOnClickListener(new SubmitPollutionEventOnClickListener());
	}

	/** Starts a submission of a pollution event */
	public class SubmitPollutionEventOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			
			startActivity(new Intent(MainScreenActivity.this, SelectImageActivity.class));
		}

	}

}