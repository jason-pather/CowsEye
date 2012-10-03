package nz.co.android.cowseye.activity;

import java.util.List;
import nz.co.android.cowseye.R;
import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.common.Constants;
import nz.co.android.cowseye.database.Incident;
import nz.co.android.cowseye.event.GetImageEvent;
import nz.co.android.cowseye.service.GetImageAsyncTask;
import nz.co.android.cowseye.view.RiverWatchGallery.ViewHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class GridIncidentGalleryActivity extends Activity {

	private Button backButton;

	private RiverWatchApplication myApplication;
	private LayoutInflater inflater;

	private List<Incident> incidents;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_incident_gallery_layout);
		
		myApplication = (RiverWatchApplication) getApplication();
		incidents = myApplication.getDatabaseAdapter().getAllIncidents();

		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflater = LayoutInflater.from(this);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));
		gridview.setOnItemClickListener(new OnItemClickListener() {
			Intent intent = new Intent(GridIncidentGalleryActivity.this,
					IncidentGalleryActivity.class);

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {

				intent.putExtra("Page Number", position);
				startActivity(intent);

			}
		});

		backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return incidents.size();
		}

		public Incident getItem(int position) {
			return incidents.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				convertView = inflater.inflate(R.layout.incident_gallery_layout_cell, null);
				holder = new ViewHolder();

				holder.imageView = (ImageView) convertView.findViewById(R.id.incident_image);
				holder.descriptionView = (TextView)convertView.findViewById(R.id.incident_description);
				holder.progressBar = (ProgressBar) convertView.findViewById(R.id.incident_progress_bar);
				
				holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				holder.imageView.setPadding(8, 8, 8, 8);
				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the View
				holder = (ViewHolder) convertView.getTag();
			}

			buildView(position, holder);
			return convertView;
		}
	}

	public class ViewHolder {
		ProgressBar progressBar;
		ImageView imageView;
		TextView descriptionView;
	}

	/* Build a View for the MyGalleryImage adapter */
	public void buildView(int position, final ViewHolder holder) {
		Incident incident = incidents.get(position);
		// try to get from local storage
		String localImageUri = incident.getLocalThumbnailUrl();
		if (localImageUri != null && !localImageUri.equals(""))
			setImage(holder, localImageUri, position);
		else {
			/**
			 * If fails to get from local storage, put a progress bar in and
			 * download
			 */
			holder.progressBar.setVisibility(View.VISIBLE);
			// launch asynctask to get image
			GetImageEvent event = new GetImageEvent(incident.getThumbnailUrl());
			new GetImageAsyncTask(myApplication, this, holder, event, position, incident.getId()).execute();
		}
	}

	public void setImage(ViewHolder holder, String pathName, int positionInArray) {
		if (pathName != null && !pathName.equals("")) {
			incidents.get(positionInArray).setLocalThumbnailUrl(pathName);
			Bitmap bm = BitmapFactory.decodeFile(pathName);
			holder.imageView.setImageBitmap(bm);
			holder.progressBar.setVisibility(View.INVISIBLE);
		}
	}

}