package nz.co.android.cowseye.service;

import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.activity.MainScreenActivity;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.event.GetIncidentsEvent;
import nz.co.android.cowseye.utility.JSONHelper;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class GetIncidentsAsyncTask extends AsyncTask<Void, Void, JSONArray> {


	private final MainScreenActivity mainScreen;
	private final GetIncidentsEvent getIncidentEvent;

	public GetIncidentsAsyncTask(MainScreenActivity mainScreen, GetIncidentsEvent getIncidentEvent){
		this.mainScreen = mainScreen;
		this.getIncidentEvent = getIncidentEvent;

	}


	protected JSONArray doInBackground(Void... Void) {
		HttpResponse response = getIncidentEvent.processRaw();
		Log.d(toString(), "response : "+response);
		JSONArray jsonArray = null;
		if(RiverWatchApplication.processSubmissionEventResponse(response)){		
			try{
				JSONObject jsonObject = JSONHelper.parseHttpResponseAsJSON(response);
				if(jsonObject.has(Constants.JSON_INCIDENTS_KEY))
					jsonArray = jsonObject.getJSONArray(Constants.JSON_INCIDENTS_KEY);
			}
			catch(Exception f){ 
				Log.e(toString(), "Exception in JsonParsing : "+f);
			}
		}
		return jsonArray;
	}

	/** Does not do anything as nothing needs to be done upon ending*/
	protected void onPostExecute(JSONArray data) {
		mainScreen.endLoadingIncidents(data);

	}
}