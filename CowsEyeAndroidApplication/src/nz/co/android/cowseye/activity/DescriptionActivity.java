package nz.co.android.cowseye.activity;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.utility.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/** The activity for inputting the description for a pollution event
 * 
 * This will allow the user to enter a description and select appropriate tags
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
        nextButton = (Button)findViewById(R.id.nextButton);
        //goes to the select location activity
        nextButton.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, RecordLocationActivity.class));
        
        //setup the spinner to choose tag type
        Spinner spinner = (Spinner) findViewById(R.id.phototag_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phototag_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    
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
            imageDescription = descriptionEditText.getText().toString();
            submissionEventBuilder.setImageDescription(imageDescription);
            System.out.println ("Sent DESCRIPTION");

        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
        .showSoftInput(descriptionEditText, InputMethodManager.SHOW_FORCED);
        }
       
        
    }

    public boolean hasDescription(){
        return !getDescription().equals("");
    }
    
    public String getDescription(){
        return descriptionEditText.getText().toString();
    }
    
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
       imageTag= (String) parent.getItemAtPosition(pos);
       submissionEventBuilder.setImageTag(imageTag);
       System.out.println ("MEAN REACHED HERE");
  
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    
}