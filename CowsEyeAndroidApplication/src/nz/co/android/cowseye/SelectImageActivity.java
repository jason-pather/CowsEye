package nz.co.android.cowseye;

import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.utility.Utils;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/** The activity for selecting an image for a pollution event submission
 * 
 * This will allow the user to either capture a new image or select an image from the gallery
 * @author lanemitc
 *
 */
public class SelectImageActivity extends Activity {

	private Button backButton;
	private Button nextButton;
	
	private Button captureImageButton;
	private Uri cameraFileUri; // holds path to the image taken or retrieved
	
	private ImageView previewImageView;
	private TextView previewTextView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_image_layout);
		setupUI();
	}

	/* Sets up the User Interface */
	private void setupUI() {
		backButton = (Button)findViewById(R.id.backButton);
		nextButton = (Button)findViewById(R.id.nextButton);
		captureImageButton = (Button)findViewById(R.id.capture_image_button);
		previewImageView = (ImageView)findViewById(R.id.preview_image);
		previewTextView = (TextView)findViewById(R.id.preview_text);

		//goes backwards
		backButton.setOnClickListener(new Utils.BackEventOnClickListener(this));
		//goes to the description activity
		nextButton.setOnClickListener(new Utils.StartNextActivityEventOnClickListener(this, DescriptionActivity.class));
		captureImageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				takeImageWithCamera();
			}
		});
	}
	
	/** Creates an intents to open the camera application and initiates it*/
	protected void takeImageWithCamera() {
		// create Intent to take a picture and return control to the calling application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// create a file to save the image
		cameraFileUri = Utils.getNewCameraFileUri();
		// set the image file name
		intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri); 
		//trigger activity
		startActivityForResult(intent, 	Constants.REQUEST_CODE_CAMERA);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Coming from capturing an image
		if (requestCode == Constants.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK){
			if (data == null || data.getData() == null) {
				//Picture has been taken, cameraFileUri holds path to photo taken
				//sets text view text to invisible
				previewTextView.setVisibility(View.INVISIBLE);
				//TODO set background preview image to image taken
//				Bitmap b = BitmapFactory.decodeFile(Uri.);
//				previewImageView.setImageBitmap(b);

				
				//TODO save this URI to an EventBuilder
			}
		}
	}
}
