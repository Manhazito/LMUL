package org.feup.bondpoint;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment {

	private static final String TAG = "MainFragment";
	public final static String DEFAULT_LOCATION_NAME = "org.feup.bondpoint.PORTO";
	public final static String DEFAULT_LOCATION_LAT = "org.feup.bondpoint.PORTO_LAT";
	public final static String DEFAULT_LOCATION_LONG = "org.feup.bondpoint.PORTO_LONG";
	private UiLifecycleHelper uiHelper;

	private String userName = "";
	private String userID = "0";
	private String userLat = "0.0";
	private String userLong = "0.0";
	private byte[][] imgsBmpByteArray = null;
	private ByteArrayOutputStream stream = new ByteArrayOutputStream();

	private String[] names = null;
	private String[] ids = null;
	private String[] latitudes = null;
	private String[] longitudes = null;

	private int nElements = 0;

	private Intent mapIntent = null;
	private ReceiveData receiveData = null;

	private ProgressDialog progressDialog = null;
	private LoginButton authButton = null;

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

		authButton = (LoginButton) view.findViewById(R.id.authButton);

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

			GPSTracker gpsTracker = new GPSTracker(this.getView().getContext());
			if (gpsTracker.canGetLocation()) {
				userLat = Double.toString(gpsTracker.getLatitude());
				userLong = Double.toString(gpsTracker.getLongitude());
			} else
				gpsTracker.showSettingsAlert();

			if (receiveData == null) {
				receiveData = new ReceiveData();
				receiveData.execute(this);
			}

		} else if (state.isClosed()) {
			progressDialog = null;
			Log.i(TAG, "Logged out...");
		}
	}

	public void showMap() {
		Log.i("MAPA", "Iniciando Map Activity...");

		String label = "";

		names[nElements - 1] = userName;
		ids[nElements - 1] = userID;
		latitudes[nElements - 1] = userLat;
		longitudes[nElements - 1] = userLong;

		mapIntent.putExtra(DEFAULT_LOCATION_NAME, "Porto");
		mapIntent.putExtra(DEFAULT_LOCATION_LAT, "41.15");
		mapIntent.putExtra(DEFAULT_LOCATION_LONG, "-8.616667");
		mapIntent.putExtra("names", names);
		mapIntent.putExtra("ids", ids);
		mapIntent.putExtra("latitudes", latitudes);
		mapIntent.putExtra("longitudes", longitudes);

		for (int i = 0; i < nElements; i++) {
			label = "picture" + i;
			mapIntent.putExtra(label, imgsBmpByteArray[i]);
			Log.d(TAG, "Enviada a imagem do amigo #" + (i + 1) + ": "
					+ imgsBmpByteArray[i]);
		}
		mapIntent.putExtra("picture", imgsBmpByteArray[nElements - 1]);

		startActivity(mapIntent);

		// mapStarted = true;
		progressDialog.dismiss();
		receiveData = null;
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

	public Session getFacebookSession() {
		return Session.getActiveSession();
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserID() {
		return userID;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public void setLatitudes(String[] latitudes) {
		this.latitudes = latitudes;
	}

	public void setLongitudes(String[] longitudes) {
		this.longitudes = longitudes;
	}

	public void setByteArray(Bitmap[] imgsBmp) {
		for (int i = 0; i < nElements; i++) {
			stream = new ByteArrayOutputStream();
			imgsBmp[i].compress(Bitmap.CompressFormat.PNG, 100, stream);
			imgsBmpByteArray[i] = stream.toByteArray();
		}
	}

	public void setnElements(int nElements) {
		this.nElements = nElements;
		this.names = new String[nElements];
		this.ids = new String[nElements];
		this.longitudes = new String[nElements];
		this.latitudes = new String[nElements];
		this.imgsBmpByteArray = new byte[nElements][];
	}
}
