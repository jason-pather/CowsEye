package nz.co.android.cowseye.activity;

import java.io.File;
import java.io.IOException;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.R.id;
import nz.co.android.cowseye.R.layout;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.utility.Utils;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/** This activity is the basis for all submission activity parts.
 *  This just provides functionality for the shared components of a submission activity
 * 
 * @author lanemitc
 *
 */
public abstract class AbstractSubmissionActivity extends Activity {

	protected Button backButton;
	protected Button nextButton;

	protected RiverWatchApplication myApplication;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApplication = (RiverWatchApplication)getApplication();
	}

	/* Sets up the User Interface */
	protected void setupUI() {
		backButton = (Button)findViewById(R.id.backButton);
		nextButton = (Button)findViewById(R.id.nextButton);
		//goes backwards
		backButton.setOnClickListener(new Utils.BackEventOnClickListener(this));
	}
	
	
	/** When the hardware back button gets pressed */
	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}


}
