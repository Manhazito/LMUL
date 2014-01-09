package org.feup.bondpoint;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class ReceiveFriends extends AsyncTask<MapActivity, Integer, Void> {

	private static final String TAG = "ReceiveFriends";

	MapActivity activity = null;

	@Override
	protected Void doInBackground(MapActivity... activities) {
		activity = activities[0];
		Session session = Session.getActiveSession();

		String fqlQuery = "SELECT uid, name, pic_small FROM user WHERE uid IN "
				+ "(SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 2)";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					@Override
					public void onCompleted(Response response) {
						Log.i(TAG, "Request completed!");
						String[] names = null;
						String[] ids = null;
						URL[] imgsURL = null;
						Bitmap[] imgsBmp = null;

						int nFriends = 0;

						try {
							if (response == null)
								throw new JSONException("");
							JSONArray data = response.getGraphObject()
									.getInnerJSONObject().getJSONArray("data");

							nFriends = data.length();

							names = new String[data.length()];
							ids = new String[data.length()];
							imgsURL = new URL[data.length()];
							imgsBmp = new Bitmap[data.length()];

							for (int i = 0; i < nFriends; i++) {
								ids[i] = data.getJSONObject(i).getString("uid");
								names[i] = data.getJSONObject(i).getString(
										"name");
								try {
									imgsURL[i] = new URL(data.getJSONObject(i)
											.getString("pic_small"));
								} catch (MalformedURLException e1) {
									e1.printStackTrace();
								}

								try {
									imgsBmp[i] = BitmapFactory
											.decodeStream(imgsURL[i]
													.openConnection()
													.getInputStream());
								} catch (IOException e) {
									e.printStackTrace();
									Log.d(TAG, "ERRO GRAVE!");
								}
							}

							Log.d(TAG, "Loaded " + nFriends + "!");

						} catch (JSONException e) {
							e.printStackTrace();
						}

						saveData(nFriends, names, ids, imgsBmp);
					}
				});

		Request.executeAndWait(request);

		return null;
	}

	private void saveData(int nElements, String[] names, String[] ids,
			Bitmap[] imgsBmp) {
		activity.setnFriends(nElements);
		activity.setFriendNames(names);
		activity.setFriendIds(ids);
		activity.setByteArray(imgsBmp);
		activity.showList();
	}
}