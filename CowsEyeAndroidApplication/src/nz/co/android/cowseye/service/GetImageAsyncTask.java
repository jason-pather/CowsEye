package nz.co.android.cowseye.service;

import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.activity.MainScreenActivity;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.event.GetImageEvent;
import nz.co.android.cowseye.event.GetIncidentsEvent;
import nz.co.android.cowseye.utility.JSONHelper;
import nz.co.android.cowseye.view.RiverWatchGallery;
import nz.co.android.cowseye.view.RiverWatchGallery.ViewHolder;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetImageAsyncTask extends AsyncTask<Void, Void, String> {


	private final RiverWatchGallery riverWatchGallery;
	private final ViewHolder holder;
	private final GetImageEvent event;

	public GetImageAsyncTask(RiverWatchGallery riverWatchGallery, RiverWatchGallery.ViewHolder holder, GetImageEvent event){
		this.riverWatchGallery = riverWatchGallery;
		this.holder = holder;
		this.event = event;
	}

	protected String doInBackground(Void... Void) {
		HttpResponse response = event.processRaw();
		Log.d(toString(), "response : "+response);
		String imagePath=null;

		if(RiverWatchApplication.processSubmissionEventResponse(response)){
			//TODO save image from input stream
		}
		return imagePath;

	}

	protected void onPostExecute(String imagePath) {
		riverWatchGallery.setImage(holder, imagePath);
	}
}