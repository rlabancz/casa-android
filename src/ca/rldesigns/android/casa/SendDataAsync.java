package ca.rldesigns.android.casa;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import ca.rldesigns.android.casa.utils.ResultCodes;

import android.os.AsyncTask;
import android.util.Log;

public class SendDataAsync extends AsyncTask<Object, Boolean, String> {
	public String TAG = "COMPASS";
	public static final int ONE_SECOND = 1000;
	public static final int RECONNECT_TIMEOUT = 30000; // 30 seconds
	public static final int DATA_TIMEOUT = 8000; // 8 seconds

	public static final int ERROR = 0;
	public static final int CONNECTING = 1;
	public static final int UPDATE = 2;

	private JSONObject jsonObject;

	private double lat;
	private double lon;
	int results;

	ArrayList<Property> properties;

	@Override
	protected void onPreExecute() {
		properties = new ArrayList<Property>();
		super.onPreExecute();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected String doInBackground(Object... params) {

		Log.d("MLS", "doInBackground");

		lat = (Double) params[1];
		lon = (Double) params[2];
		results = (Integer) params[3];
		double delta = 0.013;
		String data = "";
		try {
			String addressString = "";

			double minLat = lat - delta;
			double maxLat = lat + delta;
			double minLon = lon - delta;
			double maxLon = lon + delta;

			addressString = "http://www.realtor.ca/handlers/MapSearchHandler.ashx?xml=";
			addressString += "%3CListingSearchMap%3E" + "%3CCulture%3Een-CA%3C/Culture%3E" + "%3COrderBy%3E1%3C/OrderBy%3E"
					+ "%3COrderDirection%3EA%3C/OrderDirection%3E" + "%3CCulture%3Een-CA%3C/Culture%3E" + "%3CLatitudeMax%3E"
					+ maxLat
					+ "%3C/LatitudeMax%3E"
					+ "%3CLatitudeMin%3E"
					+ minLat
					+ "%3C/LatitudeMin%3E"
					+ "%3CLeaseRentMax%3E0%3C/LeaseRentMax%3E"
					+ "%3CLeaseRentMin%3E0%3C/LeaseRentMin%3E"
					+ "%3CListingStartDate%3E01/02/2014%3C/ListingStartDate%3E"
					+ "%3CLongitudeMax%3E"
					+ maxLon
					+ "%3C/LongitudeMax%3E"
					+ "%3CLongitudeMin%3E"
					+ minLon
					+ "%3C/LongitudeMin%3E"
					+ "%3CPriceMax%3E1000000%3C/PriceMax%3E"
					+ "%3CPriceMin%3E500000%3C/PriceMin%3E"
					+ "%3CPropertyTypeID%3E300%3C/PropertyTypeID%3E"
					+ "%3CTransactionTypeID%3E2%3C/TransactionTypeID%3E"
					+ "%3CMinBath%3E1%3C/MinBath%3E"
					+ "%3CMaxBath%3E2%3C/MaxBath%3E"
					+ "%3CMinBed%3E1%3C/MinBed%3E"
					+ "%3CMaxBed%3E2%3C/MaxBed%3E"
					+ "%3CStoriesTotalMin%3E0"
					+ "%3C/StoriesTotalMin%3E"
					+ "%3CStoriesTotalMax%3E0" + "%3C/StoriesTotalMax%3E" + "%3C/ListingSearchMap%3E";
			addressString = addressString.toString();

			if (addressString.equals(""))
				return ResultCodes.ERROR;

			HttpGet httpGet = new HttpGet(addressString);
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParameters = httpClient.getParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used.
			HttpConnectionParams.setConnectionTimeout(httpParameters, DATA_TIMEOUT);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, DATA_TIMEOUT);
			HttpContext localContext = new BasicHttpContext();

			HttpResponse response = httpClient.execute(httpGet, localContext);
			StatusLine sL = response.getStatusLine();
			String responseCode = sL.toString();
			Log.d(TAG, responseCode);
			if (responseCode.contains("200")) {
				ResponseHandler handler = new BasicResponseHandler();
				data = httpClient.execute(httpGet, handler);
				return data;
			} else {
				Log.d(TAG, responseCode);
				return ResultCodes.ERROR;
			}
		} catch (ConnectTimeoutException e) {
			Log.e(TAG, e.toString() + " CONNECT TIMEOUT");
			e.printStackTrace();
			return ResultCodes.TIMEDOUT;
		} catch (SocketTimeoutException e) {
			Log.e(TAG, e.toString() + " SOCKET TIMEOUT");
			e.printStackTrace();
			return ResultCodes.TIMEDOUT;
		} catch (UnknownHostException e) {
			Log.e(TAG, e.toString() + " UNKNOWN HOST");
			e.printStackTrace();
			return ResultCodes.TIMEDOUT;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
			return ResultCodes.ERROR;
		}
	}

	@Override
	protected void onPostExecute(String data) {

		try {
			parse(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPostExecute(data);
	}

	private boolean parse(String jsonString) throws Exception {
		jsonObject = new JSONObject(jsonString);
		if (jsonObject.has("MapSearchResults")) {
			JSONArray jArr = jsonObject.getJSONArray("MapSearchResults");

			for (int i = 0; i < jArr.length(); i++) {

				JSONObject obj = jArr.getJSONObject(i);
				String address = obj.getString("Address");

				String price = obj.getString("Price");
				double lat = obj.getDouble("Latitude");
				double lng = obj.getDouble("Longitude");
				String picture = obj.getString("PropertyImagePath");

				properties.add(new Property(address, price, lat, lng, picture));
			}

			Log.d("MLS", "done parsing");

			return true;
		} else {
			return false;
		}

	}
}