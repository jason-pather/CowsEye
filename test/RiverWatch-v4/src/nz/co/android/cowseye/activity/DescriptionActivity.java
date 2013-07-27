package nz.co.android.cowseye.activity;

import java.util.ArrayList;
import java.util.List;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.common.Constants;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The activity for inputting the description for a pollution event
 *
 * This will allow the user to enter a description and select appropriate tags
 *
 * @author lanemitc
 *
 */
public class DescriptionActivity extends AbstractSubmissionActivity {

	private EditText descriptionEditText;
	private String imageDescription;
	private String imageTag;
	protected CharSequence[] _options = { "Cow", "Dog", "Goat", "Horse",
			"Litter", "Pollution", "River", "Sheep", "Stock" };
	protected boolean[] _selections = new boolean[_options.length];
	private List<String> imageTags;

	protected Button _optionsButton;
	private List<String> tosendtags;
	private int numberOfSelections = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_layout);
		imageTags = new ArrayList<String>();

		super.onCreate(savedInstanceState);

		_optionsButton = (Button) findViewById(R.id.button);
		_optionsButton.setOnClickListener(new ButtonClickHandler());
		tosendtags = new ArrayList<String>();
		setupUI();
	}

	/* Sets up the UI */
	protected void setupUI() {
		super.setupUI();
		descriptionEditText = (EditText) findViewById(R.id.descriptionText);
		// Set text of description if we have it
		descriptionEditText.setTextColor(Color.BLACK);
		Intent intent = getIntent();
		if (intent.hasExtra(Constants.DESCRIPTION_KEY)) {
			descriptionEditText.setText(intent
					.getStringExtra(Constants.DESCRIPTION_KEY));

			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.showSoftInput(descriptionEditText,
							InputMethodManager.SHOW_FORCED);
		}
	}

	@Override
	protected void nextActivety() {
		if (!hasDescription()) {
			Toast.makeText(DescriptionActivity.this,
					getString(R.string.pleaseEnterDescription),
					Toast.LENGTH_LONG).show();
		}

		// description has been entered and recognised by user and this
		// will move the application onto the record location activity
		else {
			tosendtags.clear();
			for (int i = 0; i < _options.length; i++) {
				Log.i("ME", _options[i] + " selected: "+ _selections[i]);
				if (_selections[i]) {
					tosendtags.add((String) _options[i]);
					numberOfSelections = numberOfSelections + 1; //used to keep count of if any tags are selected or not
				}
			}

			imageDescription = descriptionEditText.getText().toString();
			submissionEventBuilder
					.setImageDescription(imageDescription);
			//checks if there has been any tags selected, else doesn't let the user progress through
			if (numberOfSelections == 0) {
				Toast toast = Toast.makeText(DescriptionActivity.this,
						getString(R.string.pleaseChooseTags),
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.LEFT, 0, -15);
				toast.show();

			}
			else {

				Intent intent = new Intent(DescriptionActivity.this,
						RecordLocationActivity.class);

				submissionEventBuilder.setImageTag(tosendtags);
				startActivity(intent);
			}
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

		System.out.println("Item was selected");
		// An item was selected. You can retrieve the selected item using
		imageTag = (String) parent.getItemAtPosition(pos);
		submissionEventBuilder.setImageTag(imageTags);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}


	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			showDialog(0);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Image Tags")
				.setMultiChoiceItems(_options, _selections,
						new DialogSelectionClickHandler())
				.setPositiveButton("OK", new DialogButtonClickHandler())
				.create();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			Log.i("ME", _options[clicked] + " selected: " + selected);
		}
	}

	public class DialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:
				printSelectedImageTags();
				break;
			}
		}
	}

	protected void printSelectedImageTags() {
		for (int i = 0; i < _options.length; i++) {
			Log.i("ME", _options[i] + " selected: " + _selections[i]);
		}
	}

}
