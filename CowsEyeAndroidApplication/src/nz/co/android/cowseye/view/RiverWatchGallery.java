package nz.co.android.cowseye.view;

import java.io.IOException;
import java.util.List;

import nz.co.android.cowseye.R;
import nz.co.android.cowseye.RiverWatchApplication;
import nz.co.android.cowseye.activity.IncidentGalleryActivity;
import nz.co.android.cowseye.utility.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * A gallery of RiverWatch incidents
 * @author Mitchell Lane
 *
 */
public class RiverWatchGallery extends Gallery {

	private List<String> imagePaths;
	private static Context context;
	private static MyGalleryImageAdapter imageAdapter;
	private RiverWatchApplication myApplication;
	private LayoutInflater inflater;
	private IncidentGalleryActivity incidentGalleryActivity;

	/* Constructors */
	public RiverWatchGallery(Context context) {
		super(context);
		this.context = context;
	}

	public RiverWatchGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public RiverWatchGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	/** Sets up the UI with the given list of image Uris 
	 * @param incidentGalleryActivity */
	public void setupUI(RiverWatchApplication app, IncidentGalleryActivity incidentGalleryActivity, List<String> imageUris){
		myApplication = app;
		this.incidentGalleryActivity = incidentGalleryActivity;
		this.context = incidentGalleryActivity;
		this.imagePaths = imageUris;
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflater = LayoutInflater.from(context);
		//Create and set the imageAdapter on this gallery
		imageAdapter = new MyGalleryImageAdapter(this);
		setAdapter(imageAdapter);
	}

	/* Tell the imageAdapter that the pages have been changed and to 
	 * refresh itself. Then change the page to the given position
	 */
	//		public void updateAdapter(int position){
	//			imageAdapter.notifyDataSetChanged();
	//			setSelection(position);
	//			slidingDrawer.redrawThumbnails(currentPageList);
	//		}
	public void refreshImageAdapter(){
		imageAdapter.notifyDataSetChanged();
	}
	public void setImageAdapterSelection(int position){
		setSelection(position);
	}

	public int getNumberOfImages() {
		return imagePaths.size();
	}

	public View getView(int position, View convertView, View parent){
		Log.d(toString(), "river watch gallery get view");
		// A ViewHolder keeps references to children views to avoid unneccessary calls
		// to findViewById() on each row.
		final ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no need
		// to reinflate it. We only inflate a new View when the convertView supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.incident_gallery_layout_cell, null);
			// Creates a ViewHolder and store references to the children views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.pageImageView = (ImageView)convertView.findViewById(R.id.incident_image);
			convertView.setTag(holder);

		} else{ 
			// Get the ViewHolder back to get fast access to the View
			holder = (ViewHolder) convertView.getTag();
		}

		buildView(position, holder);
		return convertView;
	}

	/* Build a View for the MyGalleryImage adapter */
	public void buildView(int position, final ViewHolder holder) {
		Log.d(toString(), "im : "+imagePaths.get(position));
		Bitmap bm = BitmapFactory.decodeFile(imagePaths.get(position));
		holder.pageImageView.setImageBitmap(bm);

		//		holder.pageImageView.setLayoutParams(new FrameLayout.LayoutParams(myApplication.getScreenWidth(),myApplication.getScreenHeight()));
		//		holder.pageImageView.setScaleType(ImageView.ScaleType.CENTER);
	}

	/**Scrolls the view very fast in the appropriate direction simulating a fling */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float velMax = 1800f;
		float velMin = 800f;
		float velX = Math.abs(velocityX);
		if (velX > velMax) {
			velX = velMax;
		} else if (velX < velMin) {
			velX = velMin;
		}
		velX -= 600;
		int k = 500000;
		int speed = (int) Math.floor(1f / velX * k);
		setAnimationDuration(speed);

		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			// Check if scrolling left
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			// Otherwise scrolling right
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);

		return true;
	}

	/** Scrolls the Gallery pages */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		setAnimationDuration(300);
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	/** Returns true if the user is scrolling left */
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){
		return e2.getX() > e1.getX();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		return super.onTouchEvent(event);

	}

	/** Class for dealing with changing images in the gallery's pageList */
	private class MyGalleryImageAdapter extends BaseAdapter {

		private RiverWatchGallery gallery;

		public MyGalleryImageAdapter(RiverWatchGallery g) {
			gallery = g;
		}

		public int getCount() {
			return gallery.getNumberOfImages();
		}

		public Object getItem(int position) {
			return position;
		}

		/** This method sneakily gets called by the cache of the gallery 
		 * to retrieve a cached item in the adapter */
		public long getItemId(int position) {
			// Set the selected page thumbnail
			//			slidingDrawer.setThumbnailSelection(position);
			//			currentPageList.setCurrentPageNumber(position);
			return position;
		}

		/** Gets the view at the given position in the current pageList
		 * Tries to use pre-built view if in temporary storage, otherwise it
		 * will create a new view and add it to the temporary storage for faster access
		 * next call
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(toString(), "getView in adapter");
			//Get the Gallery to make the view
			View pageView = gallery.getView(position,convertView,parent);
			return pageView;

		}


	}
	static class ViewHolder {
		ImageView pageImageView;
	}
}