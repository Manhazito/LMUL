package org.feup.bondpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class ReceiveData extends AsyncTask<MainFragment, Integer, Void> {

	private static final String TAG = "ReceiveData";
	public final static String DEFAULT_LOCATION_NAME = "org.feup.bondpoint.PORTO";
	public final static String DEFAULT_LOCATION_LAT = "org.feup.bondpoint.PORTO_LAT";
	public final static String DEFAULT_LOCATION_LONG = "org.feup.bondpoint.PORTO_LONG";

	private Request request;
	private Response response;

	private URL imgURL = null;
	private Bitmap imgBmp = null;
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();

	MainFragment fragment = null;

	@Override
	protected Void doInBackground(MainFragment... fragments) {
		Log.i(TAG, "Doing in background...");

		fragment = fragments[0];
		Session session = fragment.getFacebookSession();

		request = new Request(session, "me");
		response = request.executeAndWait();

		JSONObject obj = response.getGraphObject().getInnerJSONObject();
		fragment.setUserID(obj.optString("id"));
		fragment.setUserName(obj.optString("name"));

		imgURL = null;
		imgBmp = null;

		try {
			imgURL = new URL("http://graph.facebook.com/"
					+ fragment.getUserID() + "/picture");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			imgBmp = BitmapFactory.decodeStream(imgURL.openConnection()
					.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		imgBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		fragment.setByteArray(stream.toByteArray());

		String fqlQuery = "SELECT uid, name, current_location.latitude, current_location.longitude FROM user WHERE uid IN "
				+ "(SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 50)";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						Log.i(TAG, "Request completed!");
						String[] names = null;
						String[] ids = null;
						String[] latitudes = null;
						String[] longitudes = null;
						int nElements = 0;

						try {
							if (response == null)
								throw new JSONException("");
							JSONArray data = response.getGraphObject()
									.getInnerJSONObject().getJSONArray("data");

							nElements = data.length();

							names = new String[data.length() + 1];
							ids = new String[data.length() + 1];
							latitudes = new String[data.length() + 1];
							longitudes = new String[data.length() + 1];
							for (int i = 0; i < data.length(); i++) {
								names[i] = data.getJSONObject(i).getString(
										"name");
								ids[i] = data.getJSONObject(i).getString("uid");
								try {
									latitudes[i] = data.getJSONObject(i)
											.getJSONObject("current_location")
											.getString("latitude");
									longitudes[i] = data.getJSONObject(i)
											.getJSONObject("current_location")
											.getString("longitude");
								} catch (JSONException e) {
									latitudes[i] = "0.0";
									longitudes[i] = "0.0";
								}
								Log.i("USER " + i + 1, names[i] + " - "
										+ ids[i] + " - " + latitudes[i] + " : "
										+ longitudes[i]);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						saveData(nElements, names, ids, latitudes, longitudes);

					}
				});
		// Request.executeBatchAsync(request);
		Request.executeAndWait(request);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.i(TAG, "Showing map...");
		fragment.showMap();
	}

	private void saveData(int nElements, String[] names, String[] ids,
			String[] latitudes, String[] longitudes) {
		fragment.setnElements(nElements);

		fragment.setNames(names);
		fragment.setIds(ids);
		fragment.setLatitudes(latitudes);
		fragment.setLongitudes(longitudes);
	}

}