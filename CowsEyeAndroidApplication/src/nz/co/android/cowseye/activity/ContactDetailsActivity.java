package nz.co.android.cowseye.activity;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.R.id;
import nz.co.android.cowseye.R.layout;
import nz.co.android.cowseye.R.string;
import nz.co.android.cowseye.common.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/** The activity for inputting the description for a pollution event
 * 
 * This will allow the user to enter a description and select appropriate tags
 * @author lanemitc
 *
 */
public class ContactDetailsActivity extends Activity{

	private Button fixitButton;
	private Button doneButton;
	
	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText contactNumberEditText;
	private EditText contactEmailEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_details_layout);
		setupEditTexts();
		setupContactDetails();
		fixitButton = (Button)findViewById(R.id.backButton);
		doneButton = (Button)findViewById(R.id.doneButton);
		fixitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//go back to the details activity by finishing this activity
				//saveDetailsOnFinish(RESULT_CANCELED);
				Intent intent=new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(hasAllDetails()){
					Toast.makeText(ContactDetailsActivity.this, getResources().getString(R.string.savingContactDetails), Toast.LENGTH_SHORT).show();
					saveDetailsOnFinish(RESULT_OK);
					finish();
				}
				else
					Toast.makeText(ContactDetailsActivity.this, getResources().getString(R.string.pleaseEnterDetails), Toast.LENGTH_SHORT).show();
			}
		});
	}


	@Override
	public void onBackPressed() {
		//go back to the details activity by finishing this activity
//		saveDetailsOnFinish(RESULT_CANCELED);
		Intent intent=new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}
	
	
	private void setupEditTexts() {
		firstNameEditText = (EditText)findViewById(R.id.firstNameEditText);
		lastNameEditText = (EditText)findViewById(R.id.lastNameEditText);
		contactEmailEditText = (EditText)findViewById(R.id.contactEmailEditText);
		contactNumberEditText = (EditText)findViewById(R.id.contactNumberEditText);
	}
	
	/* Tries to load up contact details from shared preferences*/
	private void setupContactDetails() {
		SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
		firstNameEditText.setText(prefs.getString(Constants.SHARED_PREFS_FIRST_NAME, ""));
		lastNameEditText.setText(prefs.getString(Constants.SHARED_PREFS_LAST_NAME, ""));
		contactEmailEditText.setText(prefs.getString(Constants.SHARED_PREFS_EMAIL, ""));
		contactNumberEditText.setText(prefs.getString(Constants.SHARED_PREFS_NUMBER, ""));
	}
	
	private boolean hasAllDetails(){
		return !firstNameEditText.getText().toString().equals("") &&
				!lastNameEditText.getText().toString().equals("") &&
				!contactEmailEditText.getText().toString().equals("") &&
				!contactNumberEditText.getText().toString().equals("");
	}

	protected void saveDetailsOnFinish(final int RESULT_TYPE) {
		Intent intent=new Intent();
		//save  details to preferences
		SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Constants.SHARED_PREFS_FIRST_NAME, firstNameEditText.getText().toString().trim());
		editor.putString(Constants.SHARED_PREFS_LAST_NAME, lastNameEditText.getText().toString().trim());
		editor.putString(Constants.SHARED_PREFS_EMAIL, contactEmailEditText.getText().toString().trim());
		editor.putString(Constants.SHARED_PREFS_NUMBER, contactNumberEditText.getText().toString().trim());
		editor.commit();
		setResult(RESULT_TYPE, intent);
	}

}

