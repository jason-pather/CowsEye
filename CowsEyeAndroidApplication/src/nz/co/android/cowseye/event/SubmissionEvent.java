package nz.co.android.cowseye.event;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.utility.JSONHelper;
import nz.co.android.cowseye.utility.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.net.Uri;
import android.util.Log;

/**
 * Models a standard event to send to the web server
 */
public class SubmissionEvent implements Event{
    
    protected HttpPost httpPost;
    protected HttpClient client;

    protected Uri imageToPath; /* Stores the path to the image on local storage */
    protected String imageDescription;
    protected String imageTag;
    protected String address;
    protected GeoPoint geoCoordinates;

 
//    protected final String password;
//    protected final String loginCode;
    private int failCount = 0;
    protected String timeStamp;

//    public StandardEvent(String loginCode, String password, boolean authorize){
//        this.loginCode = loginCode;
//        this.password = password;
//        //create new HttpClient
//        client = constructHttpClient();
//        //Create and authorize HttpPost
//        if(authorize)
//            httpPost = setAuthorization(constructHttpPost());
//        else
//            httpPost = constructHttpPost();
//    }
    
    public SubmissionEvent(){
        client = constructHttpClient();
    }

    /** Constructs a HttpClient */
    public HttpClient constructHttpClient(){
        HttpClient client = new DefaultHttpClient();
        //set timeout to 20 seconds
        HttpConnectionParams.setConnectionTimeout(client.getParams(), TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(client.getParams(), TIMEOUT_MS);
        return client;
    }

//    /**  Authorizes the HTTP Post method */
//    public HttpPost setAuthorization(HttpPost httpPost) {
//        String auth =  loginCode+":"+password;
//        httpPost.addHeader("Authorization", "Basic " + Base64Coder.encodeString(auth).toString());
//        return httpPost;
//    }


    /** Processes the event and returns the response of the event */
    public HttpResponse processRaw() {
        //add the created method body to the post request
        httpPost.setEntity(makeEntity());
        HttpResponse response = null;
        try {
            response = client.execute(httpPost);
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
    
    /** Processes the event and returns true if successfull, otherwise false */
    public boolean processForSuccess() {
        //add the created method body to the post request
        httpPost.setEntity(makeEntity());
        HttpResponse response = null;
        try {
            response = client.execute(httpPost);
        } 
        catch (HttpResponseException e) {
            Log.e(toString(), "HttpResponseException : "+e);
        } catch (ClientProtocolException e) {
            Log.e(toString(), "ClientProtocolException : "+e);
        } catch (IOException e) {
            Log.e(toString(), "IOException : "+e);
        }
        if(response ==null){
            Log.e(toString(), "response is null: ");
            return false;
        }
        try{
            JSONObject jsonObject = JSONHelper.parseHttpResponseAsJSON(response);
            return true;
//            if(jsonObject.has(Utils.RESPONSE_CODE))
//                return ResponseCodeState.stringToResponseCode((String)jsonObject.getString(Utils.RESPONSE_CODE))==ResponseCodeState.SUCCESS;
        }
        catch(Exception e){ 
            Log.e(toString(), "Exception in JsonParsing : "+e);
        }
        return false;
    }
    
    public void incrementFailCount(){
        failCount++;
    }

    public int getFailCount(){
        return failCount;
    }
    public Uri getImagePath(){
        return imageToPath;
    }
    
    public String getImageDescription () {
    	return imageDescription;
    }
    
    public String getImageTag () {
    	return imageTag;
    }
    
    public String getTimeStamp(){
        return timeStamp;
    }


    public HttpPost setAuthorization(HttpPost httpPost) {
        // TODO Auto-generated method stub
        return null;
    }

    /** construct path to web service */
    public HttpPost constructHttpPost(){
        return new HttpPost(Constants.SUBMISSION_PATH);
    }

    
    public MultipartEntity makeEntity() {
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        try {
//            reqEntity.addPart(Constants.FORM_POST_JPG_IMAGE, new FileBody(new File(imageLocation)));
            reqEntity.addPart(Constants.FORM_TEST_STRING, new StringBody("Test_String")); 
//            reqEntity.addPart(Constants.FORM_POST_TIMESTAMP_UTC, new StringBody(timeStamp)); 
        } catch (UnsupportedEncodingException e1) {
            Log.e(toString(), "UnsupportedEncodingException : "+e1);
        }
        return reqEntity;
    }

    public void setImagePath(Uri uriToImage) {
        imageToPath = uriToImage;        
    }
    
    public void setImageDescription (String description) {
        imageDescription = description;
    }
    
    public void setImageTag (String tag) {
        imageTag=tag;
    }

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public GeoPoint getGeoCoordinates() {
		return geoCoordinates;
	}

	public void setGeoCoordinates(GeoPoint geoCoordinates) {
		this.geoCoordinates = geoCoordinates;
	}

	

}