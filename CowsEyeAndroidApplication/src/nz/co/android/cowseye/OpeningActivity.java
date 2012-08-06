package nz.co.android.cowseye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Opening screen
 * Any touch takes the user to the main details screen
 * @author Mitchell Lane
 *
 */
public class OpeningActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opening_layout);
		//Any click on the screen should just open the details activity
		findViewById(R.id.openingScreenImageButton).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(OpeningActivity.this, MainDetailsActivity.class));
			}
		});
	}
}
