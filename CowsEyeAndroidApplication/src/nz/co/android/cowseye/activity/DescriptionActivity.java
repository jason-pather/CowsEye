package nz.co.android.cowseye.activity;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.R.id;
import nz.co.android.cowseye.R.layout;
import nz.co.android.cowseye.R.string;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.utility.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/** The activity for inputting the description for a pollution event
 * 
 * This will allow the user to enter a description and select appropriate tags
 * @author lanemitc
 *
 */
public class DescriptionActivity extends AbstractSubmissionActivity {


	private EditText descriptionEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_layout);
		nextButton = (Button)findViewById(R.id.nextButton);
		//goes to the select location activity
		nextButton.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, RecordLocationActivity.class));
		setupUI();
	}

	/* Sets up the UI */
	protected void setupUI() {
		super.setupUI();
		descriptionEditText = (EditText)findViewById(R.id.descriptionText);
		//Set text of description if we have it
		Intent intent = getIntent();
		if(intent.hasExtra(Constants.DESCRIPTION_KEY)){
			descriptionEditText.setText(intent.getStringExtra(Constants.DESCRIPTION_KEY));
		}
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
		.showSoftInput(descriptionEditText, InputMethodManager.SHOW_FORCED);
	}

	public boolean hasDescription(){
		return !getDescription().equals("");
	}
	
	public String getDescription(){
		return descriptionEditText.getText().toString();
	}
	
}
