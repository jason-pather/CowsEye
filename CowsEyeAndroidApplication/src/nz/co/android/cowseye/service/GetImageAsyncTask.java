package nz.co.android.cowseye.service;

import java.io.IOException;
import java.io.InputStream;

import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.activity.MainScreenActivity;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.event.GetImageEvent;
import nz.co.android.cowseye.event.GetIncidentsEvent;
import nz.co.android.cowseye.utility.JSONHelper;
import nz.co.android.cowseye.utility.Utils;
import nz.co.android.cowseye.view.RiverWatchGallery;
import nz.co.android.cowseye.view.RiverWatchGallery.ViewHolder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class GetImageAsyncTask extends AsyncTask<Void, Void, String> {


	private final RiverWatchGallery riverWatchGallery;
	private final ViewHolder holder;
	private final GetImageEvent event;
	private final RiverWatchApplication myApplication;

	public GetImageAsyncTask(RiverWatchApplication myApplication, RiverWatchGallery riverWatchGallery, RiverWatchGallery.ViewHolder holder, GetImageEvent event){
		this.myApplication = myApplication;
		this.riverWatchGallery = riverWatchGallery;
		this.holder = holder;
		this.event = event;
	}

	protected String doInBackground(Void... Void) {
		HttpResponse response = event.processRaw();

		if(RiverWatchApplication.processEventResponse(response)){
			//save image from input stream
			return saveImageFromInputStream(response);
		}
		return null;
	}

	/**
	 * @param file path of saved image
	 */
	private String saveImageFromInputStream(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			// A Simple JSON Response Read
			InputStream instream;
			try {
				instream = entity.getContent();
				Bitmap bm = Utils.scaleBitmap(instream, 2);
				//save bitmap to filepath
				if(bm!=null){
					//TODO Save this to database with ID0.
					return myApplication.saveBitmapToDisk(bm);
				}
			} catch (IllegalStateException e) {
				Log.e(toString(), "IllegalStateException : "+e);
			} catch (IOException e) {
				Log.e(toString(), "IOException : "+e);

			}
		}
		return null;
	}

	protected void onPostExecute(String imagePath) {
		riverWatchGallery.setImage(holder, imagePath);
	}
}