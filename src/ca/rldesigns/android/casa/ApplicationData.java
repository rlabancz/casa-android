package ca.rldesigns.android.casa;

import android.content.SharedPreferences;

public class ApplicationData {
	public static final String DATABASE_NAME = "CasaData";

	public static final String SELECTED_ADDRESS = "SelectedAddress";
	public static final String SELECTED_LAT = "SelectedLat";
	public static final String SELECTED_LNG = "SelectedLng";
	public static final String RADIUS = "Radius";
	public static final String PRICE_MIN = "PriceMin";
	public static final String PRICE_MAX = "PriceMax";
	public static final String BATHROOM_MIN = "BathroomMin";
	public static final String BATHROOM_MAX = "BathroomMax";
	public static final String BEDROOM_MIN = "BedroomMin";
	public static final String BEDROOM_MAX = "BedroomMax";
	public static final String STORIES_MIN = "StoriesMin";
	public static final String STORIES_MAX = "StoriesMax";
	public static final String START_DATE_YEAR = "StartDateYear";
	public static final String START_DATE_MONTH = "StartDateMonth";
	public static final String START_DATE_DAY = "StartDateDay";

	private final SharedPreferences preferences;

	public ApplicationData(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public String getSelectedAddress() {
		return getString(SELECTED_ADDRESS);
	}

	public String getSelectedLat() {
		return getString(SELECTED_LAT);
	}

	public String getSelectedLng() {
		return getString(SELECTED_LNG);
	}

	public String getRadius() {
		return getString(RADIUS);
	}

	public String getPriceMin() {
		return getString(PRICE_MIN);
	}

	public String getPriceMax() {
		return getString(PRICE_MAX);
	}

	public String getBathroomMin() {
		return getString(BATHROOM_MIN);
	}

	public String getBathroomMax() {
		return getString(BATHROOM_MAX);
	}

	public String getBedroomMin() {
		return getString(BEDROOM_MIN);
	}

	public String getBedroomMax() {
		return getString(BEDROOM_MAX);
	}

	public String getStoriesMin() {
		return getString(STORIES_MIN);
	}

	public String getStoriesMax() {
		return getString(STORIES_MAX);
	}

	public String getStartDateYear() {
		return getString(START_DATE_YEAR);
	}

	public String getStartDateMonth() {
		return getString(START_DATE_MONTH);
	}

	public String getStartDateDay() {
		return getString(START_DATE_DAY);
	}

	private String getString(String name) {
		String result = preferences.getString(name, null);
		if (result == null)
			result = "";
		return result;
	}
}