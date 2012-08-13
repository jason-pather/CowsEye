package nz.co.android.cowseye.event;

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
	
	


}
