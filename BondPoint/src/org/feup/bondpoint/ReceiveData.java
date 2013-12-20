package org.feup.bondpoint;

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

	private URL[] imgsURL = null;
	private Bitmap[] imgsBmp = null;

	MainFragment fragment = null;

	@Override
	protected Void doInBackground(MainFragment... fragments) {
		Log.i(TAG, "Doing in background...");

		fragment = fragments[0];
		Session session = fragment.getFacebookSession();

		request = new Request(session, "me");
		response = request.executeAndWait();

		// ALTERAR!
		if (response == null) {
			Log.d(TAG, "Facebook não respondeu!");
			return null;
		}

		JSONObject obj = response.getGraphObject().getInnerJSONObject();
		fragment.setUserID(obj.optString("id"));
		fragment.setUserName(obj.optString("name"));

		imgsURL = null;
		imgsBmp = null;

		String fqlQuery = "SELECT uid, name, current_location.latitude, current_location.longitude FROM user WHERE uid IN "
				+ "(SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 5)";
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
						int nFriends = 0;

						try {
							if (response == null)
								throw new JSONException("");
							JSONArray data = response.getGraphObject()
									.getInnerJSONObject().getJSONArray("data");

							nFriends = data.length();

							names = new String[data.length() + 1];
							ids = new String[data.length() + 1];
							latitudes = new String[data.length() + 1];
							longitudes = new String[data.length() + 1];
							imgsURL = new URL[data.length() + 1];
							imgsBmp = new Bitmap[data.length() + 1];

							for (int i = 0; i < nFriends; i++) {
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

								// http://graph.facebook.com/userid/picture?type=large
								// try {
								// imgsURL[i] = new URL(
								// "http://graph.facebook.com/"
								// + ids[i]
								// + "/picture?type=small");
								// } catch (MalformedURLException e) {
								// e.printStackTrace();
								// }
								// try {
								// imgsBmp[i] = BitmapFactory
								// .decodeStream(imgsURL[i]
								// .openConnection()
								// .getInputStream());
								// } catch (IOException e) {
								// e.printStackTrace();
								// }
								//
								// break;
							}

							Log.d(TAG, "Loaded " + nFriends + ".");

							try {
								imgsURL[nFriends] = new URL(
										"http://graph.facebook.com/"
												+ fragment.getUserID()
												+ "/picture?type=large");
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							try {
								imgsBmp[nFriends] = BitmapFactory
										.decodeStream(imgsURL[nFriends]
												.openConnection()
												.getInputStream());
							} catch (IOException e) {
								e.printStackTrace();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						saveData(nFriends + 1, names, ids, latitudes,
								longitudes, imgsBmp);

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
			String[] latitudes, String[] longitudes, Bitmap[] imgsBmp) {
		fragment.setnElements(nElements);
		// fragment.setnElements(1);

		fragment.setNames(names);
		fragment.setIds(ids);
		fragment.setLatitudes(latitudes);
		fragment.setLongitudes(longitudes);
		fragment.setByteArray(imgsBmp);
	}

}