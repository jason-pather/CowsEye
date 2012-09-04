package nz.co.android.cowseye.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONObject;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.event.Event;
import nz.co.android.cowseye.event.SubmissionEvent;
import nz.co.android.cowseye.event.SubmissionEventBuilderException;
import nz.co.android.cowseye.utility.JSONHelper;
import nz.co.android.cowseye.utility.Utils;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
	private ListView tagslist;
	private int maxLength = 100;
	private List <String> imageTags;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview_layout);
		setupUI();
	}

	/* Sets up the User Interface */
	protected void setupUI() {
		super.setupUI();
		
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
					Toast.makeText(PreviewActivity.this, "SubmissionEventBuilderException error message ", Toast.LENGTH_LONG).show();
				}
			}
		});

		image = (ImageView)findViewById(R.id.PreviewImageImage);
		setPreviewImageOn(submissionEventBuilder.getImagePath());
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
	

		System.out.println ("CRASHED HERE");
		
		System.out.println ("NULL  HERE COS OF" + submissionEventBuilder.getImageTag());
		System.out.println ("PROCEEDS");
		
		StringBuffer st = new StringBuffer();
		
		for (String s: submissionEventBuilder.getImageTag()){
			st.append(s);
			if (s!=null){ st.append(","); } 
			System.out.println ("this is shit");
	
		
		}
		tag.setText(st.toString());

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
		SubmissionEvent event = submissionEventBuilder.build(); // - throws SubmissionEventBuilderException if not enough data
		//add event
		myApplication.getEventHandler().addEvent(event);
		myApplication.forceStartEventHandling();
		// go back to starting activity
		Intent intent = new Intent(this, MainScreenActivity.class);
		//Finishes all previous activities on the activity stack
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}

	public void submitEvent(Event e){
			HttpResponse response = e.processRaw();
			Log.d(toString(), "response : "+response);
			try{
				Log.d(toString(),"Status line : "+ response.getStatusLine());
				for(Header header : response.getAllHeaders()){
					Log.d(toString(),"header : "+ header.getName() + " - > "+header.getValue());
				}


				JSONObject jsonObject = JSONHelper.parseHttpResponseAsJSON(response);
				Log.d(toString(), "jsonObject : "+jsonObject);

				//				            if(jsonObject.has(Utils.RESPONSE_CODE))
				//				                return ResponseCodeState.stringToResponseCode((String)jsonObject.getString(Utils.RESPONSE_CODE))==ResponseCodeState.SUCCESS;
			}
			catch(Exception f){ 
				Log.e(toString(), "Exception in JsonParsing : "+f);
			}
	}

}
