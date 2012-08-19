package nz.co.android.cowseye.event;

import com.google.android.maps.GeoPoint;

import android.net.Uri;

/**
 * A class to help build a submission event
 * @author Mitchell Lane
 *
 */
public class SubmissionEventBuilder {

	private static SubmissionEvent submissionEvent;
	private static SubmissionEventBuilder builder = null;
	
	/** Singleton */
	public static SubmissionEventBuilder getSubmissionEventBuilder(){
		if(builder == null )
			builder = new SubmissionEventBuilder();
		return builder;
	}
	private SubmissionEventBuilder(){
		submissionEvent = new SubmissionEvent();
	}
	
	public void startNewSubmissionEvent(){
		submissionEvent = new SubmissionEvent();
	}
	public void setImagePath(Uri uriToImage) {
		submissionEvent.setImagePath(uriToImage);
	}
	public Uri getImagePath() {
		return submissionEvent.getImagePath();
	}
	public void setImageTag (String tag) {
		submissionEvent.setImageTag(tag);
	}
	public String getImageTag () {
		return submissionEvent.getImageTag();
	}
	public void setImageDescription (String descr) {
		submissionEvent.setImageDescription(descr);
	}
	public String getImageDescription () {
		return submissionEvent.getImageDescription();
	}
	public void setGeoCoordinates(GeoPoint addressCoordinates) {
		submissionEvent.setGeoCoordinates(addressCoordinates);
	}
	public void setAddress(String address) {
		submissionEvent.setAddress(address);	
	}
}
