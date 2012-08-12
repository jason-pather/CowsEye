package nz.co.android.cowseye.event;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import nz.co.android.cowseye.RiverWatchApplication;

import android.util.Log;

public class EventHandler {

	private Queue<Event> eventQueue;
	private RiverWatchApplication myApplication;

	public EventHandler(RiverWatchApplication app) {
		eventQueue = new LinkedList<Event>();
		myApplication = app;
	}

	public void addEvent(Event e) {
		eventQueue.offer(e);
	}

	/** Starts processing events sequentially */
	public void processEvents(){
		if(eventQueue.isEmpty()){
			Log.i(toString(), "No events to process");
			return;
		}
		//only process events if we have an internet connection
		if(myApplication.isOnline()){
			while(!eventQueue.isEmpty()){
				Event currentEvent = eventQueue.peek();
				Log.i(toString(), "processing : " + currentEvent.toString());
				boolean success = currentEvent.processForSuccess();
				Log.i(toString(), "successfully processed event? : "+ success);
				//only actually remove event if successful
				if(success){
					deleteImage(currentEvent);
					//update the success of event in the database
//					myApplication.getDatabaseAdapter().updateEventToProcessed(currentEvent.getTimeStamp());
					//remove event
					eventQueue.poll();
				}
				//if unsuccessful stop event handling and move the event to the end of the queue
				else{
					Event failedEvent = eventQueue.poll();
					//if we havn't reached a maximum amount of fails then keep on trying
					if(failedEvent.getFailCount()<Event.FAIL_COUNT_MAX){
						failedEvent.incrementFailCount();
						//update the fail count of the event in the database
//						myApplication.getDatabaseAdapter().updateEventFailCount(failedEvent.getTimeStamp(), failedEvent.getFailCount());
						addEvent(failedEvent);
					}
					else{
						//remove the failed event from the database
//						myApplication.getDatabaseAdapter().removeFailedEvent(failedEvent.getTimeStamp());
					}
					break;
				}
			}
		}
		else{
			//no internet connection - set delay time to 2 minutes
			Log.i(toString(), "no internet connection - delaying event timer by 2 minutes");
			myApplication.requestDelayedEventsTimer();
		}
	}

	/* Deletes the image belonging to the current event */
	private void deleteImage(Event currentEvent) {
		String filename = currentEvent.getImagePath();
		File imageFile = new File(filename);
		//delete image
		if(imageFile.exists())
			imageFile.delete();
	}

	/** Returns the amount of events awaiting upload */
	public int getNumberEventsAwaitingUpload() {
		return eventQueue.size();
	}
}
