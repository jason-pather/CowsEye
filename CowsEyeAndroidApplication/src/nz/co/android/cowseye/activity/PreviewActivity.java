package nz.co.android.cowseye.activity;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.event.SubmissionEventBuilder;
import nz.co.android.cowseye.utility.Utils;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
				//TODO Get the event out from EventBuilder
				submitPollutionEvent();
			}
		});

		image = (ImageView)findViewById(R.id.PreviewImageImage);
		image.setImageURI(submissionEventBuilder.getImagePath());
		image.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, SelectImageActivity.class));

		location = (TextView)findViewById(R.id.PreviewLocationText);
		location.setText("16 Kepler Way");
		location.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, RecordLocationActivity.class));

		description = (TextView)findViewById(R.id.PreviewDescriptionText);
		description.setText("");
		description.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, DescriptionActivity.class));
	}

	/** Submits a pollution event to the server
	 * 
	 * @param Event - the event to submit
	 */
	protected void submitPollutionEvent() {
		boolean canSubmit = false;
		//TODO
		//canSubmit = eventHandler.build() - throws buildException if not enough data
		canSubmit = true;
		if(canSubmit){
			Intent intent = new Intent(this, MainScreenActivity.class);
			//Finishes all previous activities on the activity stack
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}
		else{
			Toast.makeText(this, "BuildException error message ", Toast.LENGTH_LONG).show();
		}
	}

}
