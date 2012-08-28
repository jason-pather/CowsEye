package nz.co.android.cowseye.activity;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.utility.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * The activity for inputting the description for a pollution event
 * 
 * This will allow the user to enter a description and select appropriate tags
 * 
 * @author lanemitc
 * 
 */
public class DescriptionActivity extends AbstractSubmissionActivity implements OnItemSelectedListener {

	private EditText descriptionEditText;
	private String imageDescription;
	private String imageTag;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_layout);
		nextButton = (Button) findViewById(R.id.nextButton);

		// setup the spinner to choose tag type
		Spinner spinner = (Spinner) findViewById(R.id.phototag_spinner);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.phototag_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		setupUI();
		pressNextButton();
	}

	/* Sets up the UI */
	protected void setupUI() {
		super.setupUI();
		descriptionEditText = (EditText) findViewById(R.id.descriptionText);
		
		// Set text of description if we have it
		Intent intent = getIntent();
		if (intent.hasExtra(Constants.DESCRIPTION_KEY)) {
			descriptionEditText.setText(intent
					.getStringExtra(Constants.DESCRIPTION_KEY));
			nextButton = (Button) findViewById(R.id.nextButton);

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInput(descriptionEditText,
							InputMethodManager.SHOW_FORCED);
		}
	}

	public boolean hasDescription() {
		return !getDescription().equals("");
	}

	public String getDescription() {
		return descriptionEditText.getText().toString();
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {

		System.out.println ("Item was selected");
		// An item was selected. You can retrieve the selected item using
		imageTag = (String) parent.getItemAtPosition(pos);
		submissionEventBuilder.setImageTag(imageTag);
		Log.e("image tag is", imageTag);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	/**
	 * When the next button is hit by the user in the Describe Image screen
	 * method checks for inputs being made by the user
	 */
	public void pressNextButton() {

		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// if no description has been entered by the user, prompts to
				// enter a description
				if (!hasDescription()) {
					Toast.makeText(DescriptionActivity.this,
							getString(R.string.pleaseEnterDescription),
							Toast.LENGTH_LONG).show();
				}

				// description has been entered and recognised by user and this
				// will move the application onto the record location activity
				else {
					imageDescription = descriptionEditText.getText().toString();
					submissionEventBuilder
							.setImageDescription(imageDescription);
					System.out.println("Sent DESCRIPTION");
					Log.e("image description is", imageDescription);

					Intent intent = new Intent(DescriptionActivity.this,
							RecordLocationActivity.class);
					startActivity(intent);
				}

			}
		});

	}

}