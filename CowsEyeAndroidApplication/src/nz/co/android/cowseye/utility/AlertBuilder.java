package nz.co.android.cowseye.utility;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.activity.RecordLocationActivity;
import nz.co.android.cowseye.gps.MapManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

import com.google.android.maps.GeoPoint;

public class AlertBuilder {
	
	public static AlertDialog buildAlertMessageNoInternet(final Context context) {
		//Activity transfer to wifi settings

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getResources().getString(R.string.no_internet_message))
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.positive_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.negative_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();
	}

	public static AlertDialog buildGPSAlertMessage(final Context context, final boolean fromSubmission) {
		String message = context.getResources().getString(R.string.gps_message);
		if(fromSubmission)
			message = context.getResources().getString(R.string.gps_message_submission);
		//Activity transfer to GPS settings
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.positive_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.negative_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();
	}
	
	public static AlertDialog buildAlertMessageUpdatePosition(final RecordLocationActivity locationActivity, final MapManager mapHelper, final Context context, final String address, final GeoPoint userPoint) {
		try{
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getResources().getString(R.string.newLocationFound) +"\n"+context.getResources().getString(R.string.wouldYouLikeToUpdate)+ " " + address)
		.setCancelable(false)
		.setPositiveButton(context.getResources().getString(R.string.positive_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				locationActivity.setAddress(address,userPoint);
				mapHelper.drawUserPosition(userPoint);
				mapHelper.setMapViewToLocation(userPoint);
				dialog.cancel();
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.negative_button_title), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		return builder.create();
		}
		catch(BadTokenException e){
			//view has been destroyed
			Log.e("AlertBuilder", "Trying to alert user of new location : "+e);
		}
		return null;
	
	}

}
