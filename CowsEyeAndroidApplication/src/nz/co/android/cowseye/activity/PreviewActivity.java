package nz.co.android.cowseye.activity;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.R.id;
import nz.co.android.cowseye.R.layout;
import nz.co.android.cowseye.utility.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/** The activity for showing a preview of the pollution event
 * 
 * This will allow the user to see what they have done so far and to submit a
 * pollution event to the server
 * @author Mitchell Lane
 *
 */
public class PreviewActivity extends AbstractSubmissionActivity {
	
	private Button submitButton;
	
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
	}

	/** Submits a pollution event to the server
	 * 
	 * @param Event - the event to submit
	 */
	protected void submitPollutionEvent() {
		Intent intent = new Intent();
		setResult(RESULT_OK);
		//MUST finish all activities in stack - see friend finder
		finish();
	}
}
