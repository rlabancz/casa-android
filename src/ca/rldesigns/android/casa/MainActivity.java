package ca.rldesigns.android.casa;

import static ca.rldesigns.android.casa.ApplicationData.DATABASE_NAME;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import com.edmodo.rangebar.RangeBar;
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener;

import ca.rldesigns.android.casa.utils.ActionParams;
import ca.rldesigns.android.casa.utils.Formatter;
import ca.rldesigns.android.casa.utils.RequestCodes;
import ca.rldesigns.android.casa.utils.ResultCodes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnKeyListener, OnRangeBarChangeListener, OnClickListener {

	private SharedPreferences savedSettings;

	private Button setLocation;
	private TextView location;
	private ImageView imageView1;

	private DatePicker datePicker;
	private int year;
	private int monthOfYear;
	private int dayOfMonth;

	// Price
	private RangeBar priceRange;
	private EditText priceMin;
	private int priceMinValue;
	private EditText priceMax;
	private int priceMaxValue;

	// Bedroom
	private RangeBar bedroomRange;
	private EditText bedroomMin;
	private int bedroomMinValue;
	private EditText bedroomMax;
	private int bedroomMaxValue;

	// Bathroom
	private RangeBar bathroomRange;
	private EditText bathroomMin;
	private int bathroomMinValue;
	private EditText bathroomMax;
	private int bathroomMaxValue;

	// Bathroom
	private RangeBar storiesRange;
	private EditText storiesMin;
	private int storiesMinValue;
	private EditText storiesMax;
	private int storiesMaxValue;

	// ListingStartDate
	// LeaseRentMin
	// LeaseRentMax

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		savedSettings = getSharedPreferences(DATABASE_NAME, 0);

		setLocation = (Button) findViewById(R.id.set_location);
		setLocation.setOnClickListener(this);
		location = (TextView) findViewById(R.id.location);

		datePicker = (DatePicker) findViewById(R.id.date_picker);
		Calendar cal = Calendar.getInstance();
		long oneYear = (long) 3.154E10;
		datePicker.setMaxDate(cal.getTimeInMillis());
		datePicker.setMinDate(cal.getTimeInMillis() - oneYear);

		loadSettings();

		datePicker.updateDate(year, monthOfYear, dayOfMonth);

		// Price
		priceRange = (RangeBar) findViewById(R.id.price_range);
		priceRange.setThumbIndices(priceMinValue, priceMaxValue);
		priceRange.setOnRangeBarChangeListener(this);
		priceMin = (EditText) findViewById(R.id.price_min);
		priceMin.setText(Formatter.formatDecimal(priceRange.getLeftIndex() * 500000));
		priceMin.setOnKeyListener(this);
		priceMax = (EditText) findViewById(R.id.price_max);
		priceMax.setText(Formatter.formatDecimal(priceRange.getRightIndex() * 500000));
		priceMax.setOnKeyListener(this);
		// Bedroom
		bedroomRange = (RangeBar) findViewById(R.id.bedroom_range);
		bedroomRange.setThumbIndices(bedroomMinValue, bedroomMaxValue);
		bedroomRange.setOnRangeBarChangeListener(this);
		bedroomMin = (EditText) findViewById(R.id.bedroom_min);
		bedroomMin.setText(Integer.toString(bedroomRange.getLeftIndex()));
		bedroomMax = (EditText) findViewById(R.id.bedroom_max);
		bedroomMax.setText(Integer.toString(bedroomRange.getLeftIndex()));
		// Bathroom
		bathroomRange = (RangeBar) findViewById(R.id.bathroom_range);
		bathroomRange.setThumbIndices(bathroomMinValue, bathroomMaxValue);
		bathroomRange.setOnRangeBarChangeListener(this);
		bathroomMin = (EditText) findViewById(R.id.bathroom_min);
		bathroomMin.setText(Integer.toString(bathroomRange.getLeftIndex()));
		bathroomMax = (EditText) findViewById(R.id.bathroom_max);
		bathroomMax.setText(Integer.toString(bathroomRange.getLeftIndex()));
		// Bathroom
		storiesRange = (RangeBar) findViewById(R.id.stories_range);
		storiesRange.setThumbIndices(storiesMinValue, storiesMaxValue);
		storiesRange.setOnRangeBarChangeListener(this);
		storiesMin = (EditText) findViewById(R.id.stories_min);
		storiesMin.setText(Integer.toString(storiesRange.getLeftIndex()));
		storiesMax = (EditText) findViewById(R.id.stories_max);
		storiesMax.setText(Integer.toString(storiesRange.getLeftIndex()));

		imageView1 = (ImageView) findViewById(R.id.imageView1);
		String URL = "http://cdn.realtor.ca/listing/reb82/highres/3/c2855403_5.jpg";
		imageView1.setTag(URL);
		// new DownloadImagesTask().execute(imageView1);
		// new SendDataAsync().execute(this, location.getLatitude(), location.getLongitude(), 2);
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
		if (id == R.id.action_load) {
			loadSettings();
			return true;
		} else if (id == R.id.action_save) {
			saveSettings();
			return true;
		} else if (id == R.id.action_reset) {
			clearSettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadSettings() {
		String selectedAddress = savedSettings.getString(ApplicationData.SELECTED_ADDRESS, "");
		ActionParams.SELECTED_ADDRESS = selectedAddress;
		location.setText(selectedAddress);
		year = savedSettings.getInt(ApplicationData.START_DATE_YEAR, datePicker.getYear());
		monthOfYear = savedSettings.getInt(ApplicationData.START_DATE_MONTH, datePicker.getMonth());
		dayOfMonth = savedSettings.getInt(ApplicationData.START_DATE_DAY, datePicker.getDayOfMonth());
		priceMinValue = savedSettings.getInt(ApplicationData.PRICE_MIN, 0);
		priceMaxValue = savedSettings.getInt(ApplicationData.PRICE_MAX, 0);
		bedroomMinValue = savedSettings.getInt(ApplicationData.BEDROOM_MIN, 0);
		bedroomMaxValue = savedSettings.getInt(ApplicationData.BEDROOM_MAX, 0);
		bathroomMinValue = savedSettings.getInt(ApplicationData.BATHROOM_MIN, 0);
		bathroomMaxValue = savedSettings.getInt(ApplicationData.BATHROOM_MAX, 0);
		storiesMinValue = savedSettings.getInt(ApplicationData.STORIES_MIN, 0);
		storiesMaxValue = savedSettings.getInt(ApplicationData.STORIES_MAX, 0);
	}

	private void saveSettings() {
		SharedPreferences.Editor editor = savedSettings.edit();
		editor.putString(ApplicationData.SELECTED_ADDRESS, ActionParams.SELECTED_ADDRESS);
		editor.putInt(ApplicationData.START_DATE_YEAR, datePicker.getYear());
		editor.putInt(ApplicationData.START_DATE_MONTH, datePicker.getMonth());
		editor.putInt(ApplicationData.START_DATE_DAY, datePicker.getDayOfMonth());
		editor.putInt(ApplicationData.PRICE_MIN, priceRange.getLeftIndex());
		editor.putInt(ApplicationData.PRICE_MAX, priceRange.getRightIndex());
		editor.putInt(ApplicationData.BEDROOM_MIN, bedroomRange.getLeftIndex());
		editor.putInt(ApplicationData.BEDROOM_MAX, bedroomRange.getRightIndex());
		editor.putInt(ApplicationData.BATHROOM_MIN, bathroomRange.getLeftIndex());
		editor.putInt(ApplicationData.BATHROOM_MAX, bathroomRange.getRightIndex());
		editor.putInt(ApplicationData.STORIES_MIN, storiesRange.getLeftIndex());
		editor.putInt(ApplicationData.STORIES_MAX, storiesRange.getRightIndex());

		editor.commit();
	}

	private void clearSettings() {

	}

	@Override
	public void onIndexChangeListener(RangeBar rangeBar, int leftThumbIndex, int rightThumbIndex) {
		switch (rangeBar.getId()) {
		case R.id.price_range:
			priceMinValue = rangeBar.getLeftIndex();
			priceMin.setText(Formatter.formatDecimal(priceMinValue * 500000));
			priceMaxValue = rangeBar.getRightIndex();
			priceMax.setText(Formatter.formatDecimal(priceMaxValue * 500000));
			break;

		case R.id.bedroom_range:
			bedroomMinValue = rangeBar.getLeftIndex();
			bedroomMin.setText(Integer.toString(rangeBar.getLeftIndex()));
			bedroomMaxValue = rangeBar.getRightIndex();
			bedroomMax.setText(Integer.toString(rangeBar.getRightIndex()));
			break;

		case R.id.bathroom_range:
			bathroomMinValue = rangeBar.getLeftIndex();
			bathroomMin.setText(Integer.toString(rangeBar.getLeftIndex()));
			bathroomMaxValue = rangeBar.getRightIndex();
			bathroomMax.setText(Integer.toString(rangeBar.getRightIndex()));
			break;

		case R.id.stories_range:
			storiesMinValue = rangeBar.getLeftIndex();
			storiesMin.setText(Integer.toString(rangeBar.getLeftIndex()));
			storiesMaxValue = rangeBar.getLeftIndex();
			storiesMax.setText(Integer.toString(rangeBar.getRightIndex()));
			break;
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View view) {
		view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
		switch (view.getId()) {
		case R.id.set_location:
			Intent intentSetLocation = new Intent(this, MapPopupActivity.class);
			startActivityForResult(intentSetLocation, RequestCodes.REQUEST_MAP);
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

}