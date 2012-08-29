package nz.co.android.cowseye.view;

import java.io.IOException;
import java.util.List;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.common.Constants;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
	private static final String TAG = "Preview";

	private SurfaceHolder mHolder;
	public Camera camera;

	private boolean correctPictureSizeSet;

	private boolean resizedFrame = false;
	private double ASPECT_RATIO_WIDTH = 4.00;
	private double ASPECT_RATIO_HEIGHT = 3.00;

	private boolean isPreviewRunning = false;

	private final Display display;

	private final Activity parentActivity;

	public Preview(Activity parentActivity, Display display) {
		super(parentActivity);
		this.display = display;
		this.parentActivity = parentActivity;
		correctPictureSizeSet = false;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(toString(), " surfaceCreated :"+holder);
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		 try {
	            // This case can actually happen if the user opens and closes the camera too frequently.
	            // The problem is that we cannot really prevent this from happening as the user can easily
	            // get into a chain of activities and tries to escape using the back button.
	            // The most sensible solution would be to quit the entire EPostcard flow once the picture is sent.
	            camera = Camera.open();
	        } catch(Exception e) {
	        	Log.e(toString(), "Failed to open camera : "+e);
	        	Toast.makeText(parentActivity, parentActivity.getString(R.string.failed_to_connect_to_camera_message), Toast.LENGTH_LONG).show();
	        	parentActivity.finish();
	            return;
	        }

		try {
			camera.setPreviewDisplay(holder);

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(toString(), "could not set preview display");
		}
		camera.setPreviewCallback(null);
	}


	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		Log.e(toString(), "Surface destroyed!");
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// Now that the size is known, set up the camera parameters and begin
		// the preview.
		if(camera==null)
			return;
		
		if (isPreviewRunning)
        {
			camera.stopPreview();
			isPreviewRunning = false;
        }

        Parameters parameters = camera.getParameters();
        
        if(display.getRotation() == Surface.ROTATION_0)
        {
//            parameters.setPreviewSize(h, w);  
            setPreviewSize(h, w, parameters, false);
            camera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90)
        {
//            parameters.setPreviewSize(w, h);  
            setPreviewSize(w, h, parameters, true);

        }

        if(display.getRotation() == Surface.ROTATION_180)
        {
//            parameters.setPreviewSize(h, w);  
            setPreviewSize(h, w, parameters, false);

        }

        if(display.getRotation() == Surface.ROTATION_270)
        {
//            parameters.setPreviewSize(w, h);
            setPreviewSize(w, h, parameters, true);

            camera.setDisplayOrientation(180);
        }

//        parameters.getP
//        camera.setParameters(parameters);
        previewCamera();   

//		Camera.Parameters parameters = camera.getParameters();
//		setPreviewSize(w, h, parameters);
		setPictureSize(w, h, parameters);
//		camera.startPreview();
	}
	
	public void previewCamera()
	{        
	    try 
	    {           
	        camera.startPreview();
	        isPreviewRunning = true;
	    }
	    catch(Exception e)
	    {
	        Log.d(toString(), "Cannot start preview", e);    
	    }
	}


	private void setPreviewSize(int w, int h, Camera.Parameters parameters, boolean horizontal) {
		try{
			parameters.setPreviewSize(w, h);
			camera.setParameters(parameters);
			Log.e(toString(), "w : "+w + " h : "+h);
		}
		catch(RuntimeException e){
			Log.e(toString(), "Setting preview size initialy failed, trying alternative");
			Size s = findBestPreviewSize(h, w);
			try{
//				if(horizontal)
					parameters.setPreviewSize(s.width, s.height);
//				else
//					parameters.setPreviewSize(s.height, s.width);

			}
			catch(RuntimeException f){
				Log.e(toString(), "Second setting of preview size failed: "+f);
			}

		}
//		Log.d(toString(), "resizing frame b4");
		//if measured then don't resize frame
		if(!resizedFrame && horizontal){
//			Log.d(toString(), "resizing frame after");
			resizedFrame = true;
			//set parents dimensions to keep aspect ratio of 4:4
			ViewParent vp = getParent();
			FrameLayout f = (FrameLayout)vp;
			double frameWd =  f.getMeasuredWidth()+.00;
			double frameHt =  f.getMeasuredHeight()+.00;
			int newWd = (int)frameWd;
			int newHt = (int)frameHt;
			//Too high, reduce y
			if((frameHt/frameWd) > (ASPECT_RATIO_WIDTH/ASPECT_RATIO_HEIGHT)){
				newHt = (int)(frameWd*4/3);
			}
			//Too long, reduce X
			else{
				newWd = (int)(frameHt*4/3);
			}
//			Log.d(toString(), "newWd : "+ newWd + ", newht : " +newHt);
			if(horizontal)
				f.setLayoutParams(new FrameLayout.LayoutParams(newWd, newHt,Gravity.CENTER));
			else
				f.setLayoutParams(new FrameLayout.LayoutParams(newHt, newWd,Gravity.CENTER));

		}
	}
	private void setPictureSize(int w, int h, Camera.Parameters parameters) {
		try{
			parameters.setPictureSize(Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);
			camera.setParameters(parameters);
			correctPictureSizeSet = true;
			//			Log.d(toString(), "Trying to Set picture size");

		}
		catch(RuntimeException e){
			Log.e(toString(), "Setting picture size initialy failed, trying alternative");
			Size s = findBestPictureSize(Constants.IMAGE_WIDTH, Constants.IMAGE_HEIGHT);
			try{
				parameters.setPictureSize(s.width, s.height);
			}
			catch(RuntimeException f){
				correctPictureSizeSet = false;
				Log.e(toString(), "Second setting of picture size failed: "+f);
			}

		}
		//		Log.d(toString(), " PICTURE size : "+parameters.getPictureSize().width + ", "+parameters.getPictureSize().height);

	}

	private Size findBestPreviewSize(int desiredWidth, int desiredHeight){
		Log.d(toString(), "findBestPreviewSize wd : "+desiredWidth + ", ht : "+ desiredHeight);
		//		Log.d(toString(), "in findBestPreviewSize");
		int newWidth = Integer.MAX_VALUE;
		int newHeight = Integer.MAX_VALUE;
		int bestError = Integer.MAX_VALUE;
		Camera.Parameters parameters = camera.getParameters();
		List<Size> previewSizes = parameters.getSupportedPreviewSizes();
		if(previewSizes!=null && previewSizes.size()>0){
			Size s = previewSizes.get(0);
			newWidth = s.width;
			newHeight = s.height;
			bestError = 0;
			bestError+= Math.abs(s.width - desiredWidth);
			bestError+= Math.abs(s.height - desiredHeight);
			Log.d(toString(), "Size i "+0 + ", s :"+s.width + ", "+s.height);


		}
		for(int i = 1; i < previewSizes.size(); i++){
			Size s = previewSizes.get(i);
			int error = 0;
			error+= Math.abs(s.width - desiredWidth);
			error+= Math.abs(s.height - desiredHeight);
			if(error<bestError){
				bestError = error;
				newWidth = s.width;
				newHeight = s.height;
			}
			Log.d(toString(), "Size i "+i + ", s :"+s.width + ", "+s.height);

		}
		if(newWidth == Integer.MAX_VALUE)
			newWidth = desiredWidth;
		if(newHeight == Integer.MAX_VALUE)
			newHeight = desiredHeight;
		//		Log.d(toString(), "returning size : " +newWidth + ", " + newHeight);
		Log.d(toString(), "return findBestPreviewSize wd : "+newWidth + ", ht : "+ newHeight);

		return camera.new Size(newWidth, newHeight);

	}
	private Size findBestPictureSize(int desiredWidth, int desiredHeight){
		//		Log.d(toString(), "in findBestPictureSize");
		int newWidth = Integer.MAX_VALUE;
		int newHeight = Integer.MAX_VALUE;
		int bestError = Integer.MAX_VALUE;
		Camera.Parameters parameters = camera.getParameters();
		List<Size> pictureSizes = parameters.getSupportedPictureSizes();
		if(pictureSizes!=null && pictureSizes.size()>0){
			Size s = pictureSizes.get(0);
			newWidth = s.width;
			newHeight = s.height;
			bestError = 0;
			bestError+= Math.abs(s.width - desiredWidth);
			bestError+= Math.abs(s.height - desiredHeight);
		}
		for(int i = 1; i < pictureSizes.size(); i++){
			Size s = pictureSizes.get(i);
			int error = 0;
			error+= Math.abs(s.width - desiredWidth);
			error+= Math.abs(s.height - desiredHeight);
			if(error<bestError){
				bestError = error;
				newWidth = s.width;
				newHeight = s.height;
			}
		}
		if(newWidth == Integer.MAX_VALUE){
			newWidth = desiredWidth;
			correctPictureSizeSet = false;
		}
		if(newHeight == Integer.MAX_VALUE){
			newHeight = desiredHeight;
			correctPictureSizeSet = false;
		}
		//		Log.d(toString(), "returning size : " +newWidth + ", " + newHeight);
		return camera.new Size(newWidth, newHeight);
	}


}