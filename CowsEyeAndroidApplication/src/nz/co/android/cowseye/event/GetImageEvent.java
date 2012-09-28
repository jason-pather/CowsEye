package nz.co.android.cowseye.event;

import java.io.IOException;

import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.utility.JSONHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.util.Log;



public class GetImageEvent{

	private static final int TIMEOUT_MS = 15000;
	protected HttpGet httpGet;
	protected HttpClient client;

	private final String url;

	public GetImageEvent(String url){
		this.url = url;
		client = constructHttpClient();
		httpGet = constructHttpGet();
	}

	/** Constructs a HttpClient */
	public HttpClient constructHttpClient(){
		HttpClient client = new DefaultHttpClient();
		//set timeout to 20 seconds
		HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT_MS);
		return client;
	}

	/** Processes the event and returns the response of the event */
	public HttpResponse processRaw() {
		//add the created method body to the post request
		HttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} 
		catch (HttpResponseException e) {
			Log.e(toString(), "HttpResponseException : "+e);
		} catch (ClientProtocolException e) {
			Log.e(toString(), "ClientProtocolException : "+e);
		} catch (IOException e) {
			Log.e(toString(), "IOException : "+e);
		}
		if(response ==null)
			Log.e(toString(), "response is null: ");
		return response;
	}

	/** construct path to web service */
	public HttpGet constructHttpGet(){
		Log.d(toString(), "url : "+url);
		return new HttpGet(url);
	}
}