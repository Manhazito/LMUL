package org.feup.bondpoint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public class MainFragmentSync extends Fragment {

	private static final String TAG = "MainFragment";
	public final static String DEFAULT_LOCATION_NAME = "org.feup.bondpoint.PORTO";
	public final static String DEFAULT_LOCATION_LAT = "org.feup.bondpoint.PORTO_LAT";
	public final static String DEFAULT_LOCATION_LONG = "org.feup.bondpoint.PORTO_LONG";
	private UiLifecycleHelper uiHelper;

	private Request request;
	private String userName = "";
	private String userID = "0";
	private String userLat = "0.0";
	private String userLong = "0.0";
	private int nElements = 0;
	private static boolean mapStarted = false;

	private Intent mapIntent = null;

	private URL imgURL = null;
	private Bitmap imgBmp = null;
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();
	private byte[] byteArray = null;

	private ProgressDialog progressDialog = null;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);
		mapIntent = new Intent(view.getContext(), MapActivity.class);

		if (Build.VERSION.SDK_INT >= 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);

		// --------------------------------------------------
		// - Facebook authentication button design (360x80) -
		// --------------------------------------------------
		Typeface Medium = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/roboto_medium.ttf");
		authButton.setBackgroundResource(R.drawable.fb_button);
		authButton.setTextColor(getResources().getColor(R.color.bp_pink));
		authButton.setTextSize(20);
		authButton.setPadding(30, 20, 30, 20);
		authButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		authButton.setTypeface(Medium);

		// - End design -------------------------------------

		authButton.setFragment(this);
		authButton.clearPermissions();
		authButton.setReadPermissions(Arrays.asList("friends_location",
				"user_location", "user_friends"));

		return view;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {

			// Mostrar mensagem ao utilizador...
			if (progressDialog == null)
				progressDialog = new ProgressDialog(this.getActivity());
			progressDialog.setMessage("Processing... Please Wait...");
			progressDialog.show();

			mapIntent.putExtra("facebookSession", session);

			Log.i(TAG, "Logged in...");

			request = new Request(session, "me");
			Response response = request.executeAndWait();

			JSONObject obj = response.getGraphObject().getInnerJSONObject();
			userID = obj.optString("id");
			userName = obj.optString("name");

			GPSTracker gpsTracker = new GPSTracker(this.getView().getContext());

			if (gpsTracker.canGetLocation()) {
				userLat = Double.toString(gpsTracker.getLatitude());
				userLong = Double.toString(gpsTracker.getLongitude());
			} else
				gpsTracker.showSettingsAlert();

			imgURL = null;
			imgBmp = null;

			try {
				imgURL = new URL("http://graph.facebook.com/" + userID
						+ "/picture");
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
			byteArray = stream.toByteArray();

			File sdCardDirectory = Environment.getExternalStorageDirectory();
			File image = new File(sdCardDirectory, "test.png");
			boolean success = false;

			// Encode the file as a PNG image.
			FileOutputStream outStream;
			try {

				outStream = new FileOutputStream(image);
				imgBmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				/* 100 to keep full quality of the image */

				outStream.flush();
				outStream.close();
				success = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (success) {
				/*
				 * Toast.makeText(this.getView().getContext(),
				 * "Image saved with success", Toast.LENGTH_LONG).show();
				 */

			} else {
				Toast.makeText(this.getView().getContext(),
						"Error during image saving", Toast.LENGTH_LONG).show();
			}

			String fqlQuery = "SELECT uid, name, current_location.latitude, current_location.longitude FROM user WHERE uid IN "
					+ "(SELECT uid2 FROM friend WHERE uid1 = me() LIMIT 50)";
			Bundle params = new Bundle();
			params.putString("q", fqlQuery);
			Request request = new Request(session, "/fql", params,
					HttpMethod.GET, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							// Log.i(TAG, "Result: " + response.toString());
							String[] names = null;
							String[] ids = null;
							String[] latitudes = null;
							String[] longitudes = null;

							try {
								if (response == null)
									throw new JSONException("");
								JSONArray data = response.getGraphObject()
										.getInnerJSONObject()
										.getJSONArray("data");

								nElements = data.length();

								names = new String[data.length() + 1];
								ids = new String[data.length() + 1];
								latitudes = new String[data.length() + 1];
								longitudes = new String[data.length() + 1];
								for (int i = 0; i < data.length(); i++) {
									names[i] = data.getJSONObject(i).getString(
											"name");
									ids[i] = data.getJSONObject(i).getString(
											"uid");
									try {
										latitudes[i] = data
												.getJSONObject(i)
												.getJSONObject(
														"current_location")
												.getString("latitude");
										longitudes[i] = data
												.getJSONObject(i)
												.getJSONObject(
														"current_location")
												.getString("longitude");
									} catch (JSONException e) {
										latitudes[i] = "0.0";
										longitudes[i] = "0.0";
									}
									Log.i("USER " + i + 1, names[i] + " - "
											+ ids[i] + " - " + latitudes[i]
											+ " : " + longitudes[i]);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// Show map!
							showMap(names, ids, latitudes, longitudes);
						}
					});
			Request.executeBatchAsync(request);

		} else if (state.isClosed()) {
			progressDialog = null;
			Log.i(TAG, "Logged out...");
		}
	}

	private void showMap(String[] names, String[] ids, String[] latitudes,
			String[] longitudes) {

		if (mapStarted)
			return;
		Log.i("MAPA", "Iniciando Map Activity...");

		names[nElements] = userName;
		ids[nElements] = userID;
		latitudes[nElements] = userLat;
		longitudes[nElements] = userLong;

		mapIntent.putExtra(DEFAULT_LOCATION_NAME, "Porto");
		mapIntent.putExtra(DEFAULT_LOCATION_LAT, "41.15");
		mapIntent.putExtra(DEFAULT_LOCATION_LONG, "-8.616667");
		mapIntent.putExtra("names", names);
		mapIntent.putExtra("ids", ids);
		mapIntent.putExtra("latitudes", latitudes);
		mapIntent.putExtra("longitudes", longitudes);
		mapIntent.putExtra("userPicture", byteArray);

		startActivity(mapIntent);

		mapStarted = true;
		progressDialog.hide();
	}

	@Override
	public void onResume() {
		super.onResume();

		// For scenarios where the main activity is launched and user
		// session is not null, the session state change notification
		// may not be triggered. Trigger it if it's open/closed.
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	public static void setMapStarted(boolean m) {
		mapStarted = m;
	}
}
