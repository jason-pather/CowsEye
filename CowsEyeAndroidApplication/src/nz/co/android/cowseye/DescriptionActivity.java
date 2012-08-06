package nz.co.android.cowseye;

import nz.co.android.cowseye.common.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DescriptionActivity extends Activity {

	private Button fixitButton;
	private Button doneButton;

	private EditText descriptionEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_layout);
		fixitButton = (Button)findViewById(R.id.backButton);
		doneButton = (Button)findViewById(R.id.doneButton);
		setupDescriptionText();
		fixitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go back to the details activity by finishing this activity
				Intent intent=new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//go to the contact details screen if we have details
				if(hasDescription()){
					Toast.makeText(DescriptionActivity.this, getResources().getString(R.string.savingDescription), Toast.LENGTH_SHORT).show();
					saveDetails(RESULT_OK);
					finish();
//					Intent intent = new Intent(DescriptionActivity.this, ContactDetailsActivity.class);
//					startActivityForResult(intent, Constants.REQUEST_CODE_CONTACT_DETAILS);

				}
				//pop up a message to warn user that they have not entered a description
				else{
					Toast.makeText(DescriptionActivity.this, getResources().getString(R.string.pleaseEnterDescription),Toast.LENGTH_SHORT).show();
				}
			}
		});
	}


	private void setupDescriptionText() {
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

	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}


	private void saveDetails(final int RESULT_TYPE) {
		if(hasDescription())
			Toast.makeText(DescriptionActivity.this, getResources().getString(R.string.savingDescription), Toast.LENGTH_SHORT).show();
		//still want to save no description in case we want to delete our old one
		Intent intent=new Intent();
		intent.putExtra(Constants.DESCRIPTION_KEY, getDescription());
		setResult(RESULT_TYPE, intent);
	}
}
