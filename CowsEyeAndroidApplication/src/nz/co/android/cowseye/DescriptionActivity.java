package nz.co.android.cowseye;

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
public class DescriptionActivity extends Activity {

	private Button backButton;
	private Button nextButton;

	private EditText descriptionEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_layout);
		backButton = (Button)findViewById(R.id.backButton);
		nextButton = (Button)findViewById(R.id.doneButton);
		//goes backwards
		backButton.setOnClickListener(new Utils.BackEventOnClickListener(this));
		//goes to the select location activity
		nextButton.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, RecordLocationActivity.class));
		setupUI();
	}

	/* Sets up the UI */
	private void setupUI() {
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
	
	/** When the hardware back button gets pressed */
	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	/** Saves details of description in an intent 
	 * FIX MEE
	 * */
	private void saveDetails(final int RESULT_TYPE) {
		if(hasDescription())
			Toast.makeText(DescriptionActivity.this, getResources().getString(R.string.savingDescription), Toast.LENGTH_SHORT).show();
		//still want to save no description in case we want to delete our old one
		Intent intent=new Intent();
		intent.putExtra(Constants.DESCRIPTION_KEY, getDescription());
		setResult(RESULT_TYPE, intent);
	}
}
