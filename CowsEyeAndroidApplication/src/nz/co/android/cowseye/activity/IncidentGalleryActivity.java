package nz.co.android.cowseye.activity;

import java.util.ArrayList;
import java.util.List;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.event.SubmissionEventBuilder;
import nz.co.android.cowseye.utility.Utils;
import nz.co.android.cowseye.view.RiverWatchGallery;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

public class IncidentGalleryActivity extends Activity {

	private Button backButton;
	private RiverWatchGallery myGallery;
	private RiverWatchApplication myApplication;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incident_gallery_layout);
		setupUI();
	}

	private void setupUI() {
		backButton = (Button)findViewById(R.id.backButton);
		//goes backwards
		backButton.setOnClickListener(new Utils.BackEventOnClickListener(this));
		
		myApplication = (RiverWatchApplication)getApplication();
		myGallery = (RiverWatchGallery) (findViewById(R.id.incident_gallery));
		//TODO show ProgressDialog querying for image downloads
		List<String> imageUris = new ArrayList<String>();
		imageUris.add(SubmissionEventBuilder.getSubmissionEventBuilder().getImagePath().toString());
		imageUris.add(SubmissionEventBuilder.getSubmissionEventBuilder().getImagePath().toString());
		imageUris.add(SubmissionEventBuilder.getSubmissionEventBuilder().getImagePath().toString());

		myGallery.setupUI(myApplication, this, imageUris);
	}

}
