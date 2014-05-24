package ca.rldesigns.android.casa;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ca.rldesigns.android.casa.utils.ActionParams;
import ca.rldesigns.android.casa.utils.ResultCodes;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPopupActivity extends AbstractMapActivity implements OnInfoWindowClickListener, OnMarkerDragListener, OnClickListener {
	private GoogleMap map = null;

	private LocationManager locationManager;
	private Location currentLocation;
	private LatLng currentLatLng;
	private LatLng selectedLatLng;
	private Criteria secondaryCriteria;
	private String secondaryProvider;

	private TextView addressSelected;
	private String addressSelectedString = "";
	private Button back;
	private Button save;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load partially transparent black background
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (readyToGo()) {
			setContentView(R.layout.map_popup);
			addressSelected = (TextView) findViewById(R.id.address);
			back = (Button) findViewById(R.id.back);
			back.setOnClickListener(this);

			save = (Button) findViewById(R.id.save);
			save.setOnClickListener(this);

			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

			mapFrag.setRetainInstance(true);

			map = mapFrag.getMap();

			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			secondaryCriteria = new Criteria();
			secondaryCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
			secondaryProvider = locationManager.getBestProvider(secondaryCriteria, true);
			currentLocation = locationManager.getLastKnownLocation(secondaryProvider);

			if (savedInstanceState == null) {
				currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
				CameraUpdate center = CameraUpdateFactory.newLatLng(currentLatLng);
				CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
				getAddress(currentLatLng);
				// float accuracy = currentLocation.getAccuracy();
				// Toast.makeText(this, "Gps accuracy is " + Float.valueOf(accuracy).toString() + " meters.", Toast.LENGTH_LONG).show();
				Toast.makeText(this, "Drag pin to select a location", Toast.LENGTH_LONG).show();

				map.moveCamera(center);
				map.animateCamera(zoom);
				map.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
						.title("Your Location").draggable(true));
				selectedLatLng = currentLatLng;
			}

			// map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
			map.setOnInfoWindowClickListener(this);
			map.setOnMarkerDragListener(this);
			map.setMyLocationEnabled(true);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
	}

	// instant of pickup
	@Override
	public void onMarkerDragStart(Marker marker) {
	}

	// while dragging
	@Override
	public void onMarkerDrag(Marker marker) {
	}

	// when released
	@Override
	public void onMarkerDragEnd(Marker marker) {
		LatLng position = marker.getPosition();
		getAddress(position);
		selectedLatLng = position;
	}

	private boolean getAddress(LatLng position) {
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(position.latitude, position.longitude, 1);
			if (addresses.size() > 0) {
				addressSelectedString = addresses.get(0).getAddressLine(0);
				addressSelected.setText(addressSelectedString);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
		switch (view.getId()) {
		case R.id.back:
			this.finish();
			break;

		case R.id.save:
			ActionParams.SELECTED_POSITION = selectedLatLng;
			ActionParams.SELECTED_ADDRESS = addressSelectedString;
			setResult(ResultCodes.NEW_ADDRESS);
			this.finish();
			break;
		}
	}
}