package nz.co.android.cowseye.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.view.Preview;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * This is used for natively taking a picture via the camera on the phone or tablet
 * @author Mitchell Lane
 *
 */
public class TakePictureActivity  extends Activity{

	private Button backButton;
	private Button captureButton;
	private Preview preview;
	private boolean pictureTaken = false;
	private Display display;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_picture);
		Bundle extras = getIntent().getExtras();
		//		myApplication = getApplication();
		setupUI();
	}

	public void setupUI(){
		captureButton = (Button)findViewById(R.id.capture_image_button);
		backButton = (Button)findViewById(R.id.backButton);

		display = ((WindowManager)getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();
		preview = new Preview(this,display);
		FrameLayout previewFrameLayout = ((FrameLayout) findViewById(R.id.previewFrameLayout));
		previewFrameLayout.addView(preview);
		captureButton.setOnClickListener(new CaptureOnClickListener());
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				endActivityUnsuccessfully();
			}
		});
	}

	private class CaptureOnClickListener implements OnClickListener {
		public void onClick(View v) {
			if(!pictureTaken){
				pictureTaken = true;
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		} 
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(toString(), "onShutter'd");
		}
	};

	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			try{
				String pathname = null;
				boolean rotateBitmap = false;
				int rotateAmount = 0;

				if(display.getRotation() == Surface.ROTATION_0)
				{
					rotateBitmap= true;
					rotateAmount = 90;
				}
				else if(display.getRotation() == Surface.ROTATION_180)
				{
					rotateBitmap= true;
					rotateAmount = 270;            
				}
				else if(display.getRotation() == Surface.ROTATION_270)
				{
					rotateBitmap= true;
					rotateAmount = 180;
				}
				if(rotateBitmap){
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					if(bitmap==null){
						Toast.makeText(TakePictureActivity.this, getString(R.string.failed_camera_please_try_again), Toast.LENGTH_LONG).show();
						endActivityUnsuccessfully();
					}
					//rotate matrix
					Matrix matrix = new Matrix();
					matrix.postRotate(rotateAmount); 
					// create a new bitmap from the original using the matrix to transform the result
					Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
					try {
						pathname = saveBitmapToDisk(rotatedBitmap);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else
					pathname = savePictureToDisk(data);
				pictureTaken = false;
				if(pathname==null)
					endActivityUnsuccessfully();
				else{
					endActivitySuccessfully(pathname);
				}
			}
			catch(IOException e){
				Log.e(toString(), "IOException : " +e);
				endActivityUnsuccessfully();
			}
		}

		private String saveBitmapToDisk(Bitmap rotatedBitmap) throws IOException {
			try{
				final long num = System.currentTimeMillis();
				final String ID = getString(R.string.app_name) +num;
				File dir = TakePictureActivity.this.getDir("", Context.MODE_PRIVATE);
				String pathToDir = dir.getAbsolutePath();
				final String pathName = pathToDir + File.separator+ ID;
				FileOutputStream out = new FileOutputStream(pathName);
				rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
				return pathName;
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			throw new IOException("Could not create file or could not write to created file");
		}

		private String savePictureToDisk(byte[] data) throws IOException{
			FileOutputStream outStream = null;
			try {
				// write to local file system
				final long num = System.currentTimeMillis();
				final String ID = getString(R.string.app_name) +num;
				File dir = TakePictureActivity.this.getDir("", Context.MODE_PRIVATE);
				String pathToDir = dir.getAbsolutePath();
				final String pathName = pathToDir + File.separator+ ID;
				outStream = new FileOutputStream(String.format(
						"%s.jpg", pathName));
				outStream.write(data);
				outStream.close();
				Log.d(toString(), "onPictureTaken - wrote bytes: " + data.length);
				return String.format("%s.jpg", pathName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			throw new IOException("Could not create file or could not write to created file");
		}
	};

	private void endActivitySuccessfully(String filePathName) {
		Log.d(toString(), "endActivitySuccessfully");
		Intent i = new Intent();
		i.putExtra(Constants.IMAGE_URI_KEY, filePathName);
		setResult(RESULT_OK, i);
		finish();
	}

	private void endActivityUnsuccessfully() {
		Log.d(toString(), "endActivityUnsuccessfully");
		Intent i = new Intent();
		setResult(RESULT_CANCELED, i);
		finish();
	}

	@Override
	public void onBackPressed() {
		endActivityUnsuccessfully();
	}
	

}

