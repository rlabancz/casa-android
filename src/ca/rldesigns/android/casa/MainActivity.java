package ca.rldesigns.android.casa;

import static ca.rldesigns.android.casa.ApplicationData.DATABASE_NAME;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.edmodo.rangebar.RangeBar;
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.Contents;
import com.google.zxing.client.android.Intents;

import ca.rldesigns.android.casa.utils.ActionParams;
import ca.rldesigns.android.casa.utils.Formatter;
import ca.rldesigns.android.casa.utils.RequestCodes;
import ca.rldesigns.android.casa.utils.ResultCodes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnKeyListener, OnSeekBarChangeListener, OnRangeBarChangeListener, OnClickListener {

	private SharedPreferences savedSettings;

	private Button setLocation;
	private TextView location;
	private LatLng selectedLatLng;

	private DatePicker datePicker;
	private int year;
	private int monthOfYear;
	private int dayOfMonth;

	// Radius
	private SeekBar radiusRange;
	private EditText radiusMax;
	private int radiusValue;

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

		// Radius
		radiusRange = (SeekBar) findViewById(R.id.radius_range);
		radiusRange.setProgress(radiusValue);
		radiusRange.setOnSeekBarChangeListener(this);
		radiusMax = (EditText) findViewById(R.id.radius_max);
		radiusMax.setText(Formatter.formatRadius(radiusRange.getProgress()));
		radiusMax.setOnKeyListener(this);

		// Price
		priceRange = (RangeBar) findViewById(R.id.price_range);
		priceRange.setThumbIndices(priceMinValue, priceMaxValue);
		priceRange.setOnRangeBarChangeListener(this);
		priceMin = (EditText) findViewById(R.id.price_min);
		priceMin.setText(Formatter.formatDecimal(priceMinValue * 500000));
		priceMin.setOnKeyListener(this);
		priceMax = (EditText) findViewById(R.id.price_max);
		priceMax.setText(Formatter.formatDecimal(priceMaxValue * 500000));
		priceMax.setOnKeyListener(this);
		// Bedroom
		bedroomRange = (RangeBar) findViewById(R.id.bedroom_range);
		bedroomRange.setThumbIndices(bedroomMinValue, bedroomMaxValue);
		bedroomRange.setOnRangeBarChangeListener(this);
		bedroomMin = (EditText) findViewById(R.id.bedroom_min);
		bedroomMin.setText(Integer.toString(bedroomMinValue));
		bedroomMin.setOnKeyListener(this);
		bedroomMax = (EditText) findViewById(R.id.bedroom_max);
		bedroomMax.setText(Integer.toString(bedroomMaxValue));
		bedroomMax.setOnKeyListener(this);
		// Bathroom
		bathroomRange = (RangeBar) findViewById(R.id.bathroom_range);
		bathroomRange.setThumbIndices(bathroomMinValue, bathroomMaxValue);
		bathroomRange.setOnRangeBarChangeListener(this);
		bathroomMin = (EditText) findViewById(R.id.bathroom_min);
		bathroomMin.setText(Integer.toString(bathroomMinValue));
		bathroomMax = (EditText) findViewById(R.id.bathroom_max);
		bathroomMax.setText(Integer.toString(bathroomMaxValue));
		// Bathroom
		storiesRange = (RangeBar) findViewById(R.id.stories_range);
		storiesRange.setThumbIndices(storiesMinValue, storiesMaxValue);
		storiesRange.setOnRangeBarChangeListener(this);
		storiesMin = (EditText) findViewById(R.id.stories_min);
		storiesMin.setText(Integer.toString(storiesMinValue));
		storiesMax = (EditText) findViewById(R.id.stories_max);
		storiesMax.setText(Integer.toString(storiesMaxValue));
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
		} else if (id == R.id.action_export) {
			exportSettings();
			return true;
		} else if (id == R.id.action_reset) {
			clearSettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadSettings() {
		ActionParams.SELECTED_ADDRESS = savedSettings.getString(ApplicationData.SELECTED_ADDRESS, "");
		location.setText(ActionParams.SELECTED_ADDRESS);
		double lat = savedSettings.getFloat(ApplicationData.SELECTED_LAT, 0.0f);
		double lng = savedSettings.getFloat(ApplicationData.SELECTED_LNG, 0.0f);
		selectedLatLng = new LatLng(lat, lng);
		radiusValue = savedSettings.getInt(ApplicationData.RADIUS, 0);
		priceMinValue = savedSettings.getInt(ApplicationData.PRICE_MIN, 0);
		priceMaxValue = savedSettings.getInt(ApplicationData.PRICE_MAX, 0);
		bedroomMinValue = savedSettings.getInt(ApplicationData.BEDROOM_MIN, 0);
		bedroomMaxValue = savedSettings.getInt(ApplicationData.BEDROOM_MAX, 0);
		bathroomMinValue = savedSettings.getInt(ApplicationData.BATHROOM_MIN, 0);
		bathroomMaxValue = savedSettings.getInt(ApplicationData.BATHROOM_MAX, 0);
		storiesMinValue = savedSettings.getInt(ApplicationData.STORIES_MIN, 0);
		storiesMaxValue = savedSettings.getInt(ApplicationData.STORIES_MAX, 0);
		year = savedSettings.getInt(ApplicationData.START_DATE_YEAR, datePicker.getYear());
		monthOfYear = savedSettings.getInt(ApplicationData.START_DATE_MONTH, datePicker.getMonth());
		dayOfMonth = savedSettings.getInt(ApplicationData.START_DATE_DAY, datePicker.getDayOfMonth());
	}

	private void saveSettings() {
		SharedPreferences.Editor editor = savedSettings.edit();
		editor.putFloat(ApplicationData.SELECTED_LAT, Float.parseFloat(Formatter.formatCoordinate(selectedLatLng.latitude)));
		editor.putFloat(ApplicationData.SELECTED_LNG, Float.parseFloat(Formatter.formatCoordinate(selectedLatLng.longitude)));
		editor.putString(ApplicationData.SELECTED_ADDRESS, ActionParams.SELECTED_ADDRESS);
		editor.putInt(ApplicationData.RADIUS, radiusRange.getProgress());
		editor.putInt(ApplicationData.PRICE_MIN, priceRange.getLeftIndex());
		editor.putInt(ApplicationData.PRICE_MAX, priceRange.getRightIndex());
		editor.putInt(ApplicationData.BEDROOM_MIN, bedroomRange.getLeftIndex());
		editor.putInt(ApplicationData.BEDROOM_MAX, bedroomRange.getRightIndex());
		editor.putInt(ApplicationData.BATHROOM_MIN, bathroomRange.getLeftIndex());
		editor.putInt(ApplicationData.BATHROOM_MAX, bathroomRange.getRightIndex());
		editor.putInt(ApplicationData.STORIES_MIN, storiesRange.getLeftIndex());
		editor.putInt(ApplicationData.STORIES_MAX, storiesRange.getRightIndex());
		editor.putInt(ApplicationData.START_DATE_YEAR, datePicker.getYear());
		editor.putInt(ApplicationData.START_DATE_MONTH, datePicker.getMonth());
		editor.putInt(ApplicationData.START_DATE_DAY, datePicker.getDayOfMonth());
		editor.commit();
	}

	private void exportSettings() {
		JSONObject settings = new JSONObject();
		try {
			settings.put("x", Formatter.formatCoordinate(selectedLatLng.longitude));
			settings.put("y", Formatter.formatCoordinate(selectedLatLng.latitude));
			settings.put("r", radiusRange.getProgress());
			settings.put("p-", priceRange.getLeftIndex());
			settings.put("p+", priceRange.getRightIndex());
			settings.put("e-", bedroomRange.getLeftIndex());
			settings.put("e+", bedroomRange.getRightIndex());
			settings.put("a-", bathroomRange.getLeftIndex());
			settings.put("a+", bathroomRange.getRightIndex());
			settings.put("s-", storiesRange.getLeftIndex());
			settings.put("s+", storiesRange.getRightIndex());
			settings.put(
					"d",
					Integer.toString(datePicker.getMonth()) + "." + Integer.toString(datePicker.getDayOfMonth()) + "."
							+ Integer.toString(datePicker.getYear()));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String out = settings.toString();
		Intent intent = new Intent(Intents.Encode.ACTION);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, out);
		intent.putExtra(Intents.Encode.FORMAT, BarcodeFormat.QR_CODE.toString());
		startActivity(intent);
	}

	private void clearSettings() {

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.radius_range:
			radiusValue = progress;
			radiusMax.setText(Formatter.formatRadius(radiusValue));
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
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
				selectedLatLng = ActionParams.SELECTED_POSITION;
			}
			break;
		}
	}
}