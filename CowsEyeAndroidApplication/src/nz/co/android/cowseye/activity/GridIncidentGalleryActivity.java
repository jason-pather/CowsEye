package nz.co.android.cowseye.activity;

import java.util.List;
import nz.co.android.cowseye.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class GridIncidentGalleryActivity extends Activity {

	private Button backButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_incident_gallery_layout);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub

			}

		});

		backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				onBackPressed();

			}

		});

	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private List<String> healthyFoods;

		public ImageAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public String getItem(int position) {
			return healthyFoods.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some
										// attributes
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mThumbIds[position]);
			return imageView;
		}

		// references to our images
		private Integer[] mThumbIds = { R.drawable.logo, R.drawable.riverwatch,
				R.drawable.cowgrazing, R.drawable.logo, R.drawable.riverwatch,
				R.drawable.cowgrazing, R.drawable.logo, R.drawable.logo,
				R.drawable.riverwatch, R.drawable.cowgrazing, R.drawable.logo,
				R.drawable.riverwatch, };

	}
}