package ca.rldesigns.android.casa;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import ca.rldesigns.android.casa.utils.ActionParams;
import ca.rldesigns.android.casa.utils.RequestCodes;
import ca.rldesigns.android.casa.utils.ResultCodes;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	LocationManager locationManager;

	Drawable backgroundD;

	// ListingStartDate
	// PriceMax
	// PriceMin
	// MinBath
	// MaxBath
	// MinBed
	// MaxBed
	// StoriesTotalMin
	// StoriesTotalMax
	/*
	 * 
	 * "<OrderBy>1</OrderBy>" + "<OrderDirection>A</OrderDirection>" + "<Culture>en-CA</Culture>" + "<LatitudeMax>"
	 * 
	 * + "<LeaseRentMax>0</LeaseRentMax>" + "<LeaseRentMin>0</LeaseRentMin>"
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

		ImageView imageView = null;

		@Override
		protected Bitmap doInBackground(ImageView... imageViews) {
			this.imageView = imageViews[0];
			return download_Image((String) imageView.getTag());
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
		}

		private Bitmap download_Image(String url) {
			// ---------------------------------------------------
			Bitmap bm = null;
			try {
				URL aURL = new URL(url);
				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bm = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
			} catch (IOException e) {
				Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
			}
			return bm;
			// ---------------------------------------------------
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener {

		private Button setLocation;

		private TextView location;
		private ImageView imageView1;
		private RelativeLayout background;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_main, container, false);
			setLocation = (Button) view.findViewById(R.id.set_location);
			setLocation.setOnClickListener(this);

			location = (TextView) view.findViewById(R.id.location);

			background = (RelativeLayout) view.findViewById(R.id.background);
			imageView1 = (ImageView) view.findViewById(R.id.imageView1);
			String URL = "http://cdn.realtor.ca/listing/reb82/highres/3/c2855403_5.jpg";
			imageView1.setTag(URL);
			// new DownloadImagesTask().execute(imageView1);

			// new SendDataAsync().execute(this, location.getLatitude(), location.getLongitude(), 2);

			return view;
		}

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
			switch (view.getId()) {
			case R.id.set_location:
				Intent intentNewGame = new Intent(getActivity(), MapPopupActivity.class);
				startActivityForResult(intentNewGame, RequestCodes.REQUEST_MAP);
				break;
			}
		}

		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			switch (requestCode) {
			case RequestCodes.REQUEST_MAP:
				if (resultCode == ResultCodes.NEW_ADDRESS) {
					location.setText(ActionParams.SELECTED_ADDRESS);
				}
				break;
			}
		}
	}
}