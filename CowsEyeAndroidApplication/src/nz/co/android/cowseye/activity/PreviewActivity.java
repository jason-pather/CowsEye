package nz.co.android.cowseye.activity;

import java.io.IOException;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.event.Event;
import nz.co.android.cowseye.event.GetIncidentsEvent;
import nz.co.android.cowseye.event.SubmissionEvent;
import nz.co.android.cowseye.event.SubmissionEventBuilderException;
import nz.co.android.cowseye.service.GetIncidentsAsyncTask;
import nz.co.android.cowseye.utility.AlertBuilder;
import nz.co.android.cowseye.utility.JSONHelper;
import nz.co.android.cowseye.utility.Utils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

/** The activity for showing a preview of the pollution event
 * 
 * This will allow the user to see what they have done so far and to submit a
 * pollution event to the server
 * @author Mitchell Lane
 *
 */
public class PreviewActivity extends AbstractSubmissionActivity {

	private Button submitButton;
	private ImageView image;
	private TextView location;
	private TextView description;
	private TextView tag;
	//	private ListView tagslist;
	private int maxLength = 1000;
	//	private List <String> imageTags;
	private ProgressDialog progressDialog;
	private Handler handler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_layout);
		handler = new Handler();
		setupUI();
	}

	/* Sets up the User Interface */
	protected void setupUI() {
		super.setupUI();
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setTitle(getString(R.string.sending_incident_title));
		progressDialog.setMessage(getString(R.string.sending_incident_msg));
		submitButton = (Button)findViewById(R.id.submit_button);
		//sends the event to the server
		submitButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				try{
					//attempt to submit a pollution event
					submitPollutionEvent();
				}
				catch(SubmissionEventBuilderException e){
					Toast.makeText(PreviewActivity.this, "You have not succesfully given all required information", Toast.LENGTH_LONG).show();
				}
			}
		});

		image = (ImageView)findViewById(R.id.PreviewImageImage);
		setPreviewImageOn(Uri.parse(submissionEventBuilder.getImagePath().toString()));
		//		image.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, SelectImageActivity.class));

		location = (TextView)findViewById(R.id.PreviewLocationText);
		GeoPoint geoPoint = submissionEventBuilder.getGeoCoordinates();
		if(geoPoint!=null){ //try and set geo coordinate location first
			double geoPointLat = geoPoint.getLatitudeE6()/1E6;
			double geoPointLon = geoPoint.getLongitudeE6()/1E6;
			location.setText(String.format("%s %.2f, %.2f", getString(R.string.geocoordinates_text), geoPointLat, geoPointLon));
		}

		else //otherwise set address
			location.setText(submissionEventBuilder.getAddress());
		//location.setText("16 Kepler Way");
		//		location.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, RecordLocationActivity.class));

		description = (TextView)findViewById(R.id.PreviewDescriptionText);
		description.setMovementMethod(new ScrollingMovementMethod());
		String descriptionText = submissionEventBuilder.getImageDescription();
		if (descriptionText.length() > maxLength) descriptionText = descriptionText.substring(0, maxLength);
		description.setText(submissionEventBuilder.getImageDescription());

		//		description.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, DescriptionActivity.class));

		tag = (TextView)findViewById(R.id.PreviewImageTag);

		StringBuffer st = new StringBuffer();

		for (String s: submissionEventBuilder.getImageTag()){
			if (s!=null){ 
				st.append(s);
				st.append(", "); 
			} 
		}


		String text = (String) st.toString();
		if(text.length()>0)
			text = text.substring(0, text.length()-2);
		System.out.println ("Text value is   " + text);
		String ntext = text.substring(0,text.length());
		tag.setText(ntext);


		tag.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, DescriptionActivity.class));


	}

	/** Enables the preview image, first by trying to decode the URI natively into a bitmap 
	 * If this fails then the image will be loaded from the uri handled by the system
	 * @param cameraFileUri - path to the image
	 */
	private void setPreviewImageOn(Uri cameraFileUri) {
		try{
			Bitmap b = Utils.getAppFriendlyBitmap(cameraFileUri, getContentResolver());
			if(b==null)
				throw new IOException("Bitmap returned is null");
			image.setImageBitmap(b);
		}
		catch(IOException e){
			Log.e(toString(), "bitmap failed to decode : "+e);
			image.setImageURI(cameraFileUri);
		}	
	}


	/** Submits a pollution event to the server
	 * 
	 * @throws SubmissionEventBuilderException if not enough data
	 */
	protected void submitPollutionEvent() throws SubmissionEventBuilderException{
		final SubmissionEvent currentEvent = submissionEventBuilder.build(); // - throws SubmissionEventBuilderException if not enough data
		if(myApplication.isOnline()){
			progressDialog.show();

			new Thread(new Runnable() {
				@Override
				public void run() {
					final boolean success = RiverWatchApplication.processEventResponse(currentEvent.processRaw());
					handler.post(new Runnable() {

						@Override
						public void run() {
							Log.i(toString(), "successfully processed event? : "+ success);
							//only actually remove event if successful
							if(success){
								Toast.makeText(PreviewActivity.this, getString(R.string.success_submission_msg), Toast.LENGTH_LONG).show();
								myApplication.deleteImage(currentEvent);
								new GetIncidentsAsyncTask(new GetIncidentsEvent(myApplication, 0, 50),myApplication).execute();
								// go back to starting activity
								Intent intent = new Intent(PreviewActivity.this, MainScreenActivity.class);
								//Finishes all previous activities on the activity stack
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
								startActivity(intent);
								finish();
							}
							//if unsuccessful stop event handling and move the event to the end of the queue
							else{
								Toast.makeText(PreviewActivity.this, getString(R.string.failure_submission_msg), Toast.LENGTH_LONG).show();
							}
							progressDialog.dismiss();
						}
					});
				}
			}).start();
		}
		else
			AlertBuilder.buildAlertMessageNoInternet(this).show();

	}


}
