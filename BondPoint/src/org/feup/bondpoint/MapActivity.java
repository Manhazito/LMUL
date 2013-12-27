package org.feup.bondpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements OnMapLongClickListener,
		OnMapClickListener, OnMarkerClickListener {
	private static final String TAG = "MapActivity";

	private static String bp_title = "New BondPoint";

	private Intent bpIntent = null;

	private boolean marker = false;

	Resources resources = null;
	private Intent mapIntent = null;
	GoogleMap map;
	private View mapView = null;

	private String[] namesStr = null;
	private String[] idsStr = null;
	private String[] latitudesStr = null;
	private String[] longitudesStr = null;
	private Bitmap[] imgsBmp = null;
	private UiLifecycleHelper uiHelper;

	// private View userMarkerLayout = null;
	// private ImageView userMarkerPic = null;
	private View avFriendMarkerLayout = null;
	private ImageView avFriendMarkerPic = null;

	private int nFriends = 0;
	private String label = "";
	private LatLng userLocation = null;

	// private Bitmap userMaskBmp = null;
	private Bitmap friendMaskBmp = null;
	private Bitmap bp = null;
	// private Bitmap scaledUserMaskBmp = null;
	private Bitmap scaledFriendMaskBmp = null;
	private Bitmap userMarkerBmp = null;
	private Bitmap friendMarkerBmp = null;
	private Bitmap squaredUserBmp = null;
	private Bitmap squaredFriendBmp = null;
	// private Canvas userCanvas = null;
	private Canvas friendCanvas = null;
	// private Paint userPaint = null;
	private Paint friendPaint = null;
	// private Bitmap userResultBmp = null;
	private Bitmap friendResultBmp = null;

	private Button logoutBtn = null;
	private ImageView userTopImage = null;
	private TextView userTopName = null;

	private byte[][] imgsBmpByteArray = null;

	private Boolean friendsWithoutCoordinates = false;

	private Session session;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);

		mapView = findViewById(android.R.id.content).getRootView();

		bpIntent = new Intent(mapView.getContext(), ConnectActivity.class);

		logoutBtn = (Button) mapView.findViewById(R.id.logout);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setInfoWindowAdapter(new MyInfoWindowAdapter(this));

		// userMarkerLayout = ((LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE))
		// .inflate(R.layout.user_marker, null);
		avFriendMarkerLayout = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.av_friend_marker, null);

		resources = mapView.getResources();

		logoutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				session = Session.getActiveSession();
				session.closeAndClearTokenInformation();
				finish();
			}
		});

		map.setOnMapLongClickListener(this);
		map.setOnMapClickListener(this);

		map.setOnMarkerClickListener(this);

		mapIntent = getIntent();

		namesStr = mapIntent.getStringArrayExtra("names");
		idsStr = mapIntent.getStringArrayExtra("ids");
		latitudesStr = mapIntent.getStringArrayExtra("latitudes");
		longitudesStr = mapIntent.getStringArrayExtra("longitudes");

		nFriends = namesStr.length - 1;
		label = "";

		imgsBmpByteArray = new byte[nFriends + 1][];
		imgsBmp = new Bitmap[nFriends + 1];

		Log.d(TAG, "Loaded " + nFriends + ".");

		for (int i = 0; i <= nFriends; i++) {
			label = "picture" + i;
			imgsBmpByteArray[i] = mapIntent.getByteArrayExtra(label);
			if (imgsBmpByteArray[i] != null) {
				// Log.d(TAG, "Friend " + i + "-> Label: " + label + ".");
				imgsBmp[i] = BitmapFactory.decodeByteArray(imgsBmpByteArray[i],
						0, imgsBmpByteArray[i].length);
			} else {
				// Log.d(TAG, "Friend " + i + "-> no picyure!");
				if (i == nFriends) { // User Picture!
					imgsBmp[i] = BitmapFactory.decodeResource(resources,
							R.drawable.user_no_pic);
				} else {
					imgsBmp[i] = BitmapFactory.decodeResource(resources,
							R.drawable.av_no_pic);
				}
			}
		}

		userLocation = new LatLng(Double.parseDouble(latitudesStr[nFriends]),
				Double.parseDouble(longitudesStr[nFriends]));

		// -------------------------------------
		// Friends marker with mask
		// For now they all appear as AVailable
		// -------------------------------------
		friendsWithoutCoordinates = false;
		friendMarkerBmp = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(resources, R.drawable.av), 50, 50,
				true);
		for (int i = 0; i < nFriends; i++) {
			Log.i("PEOPLE", namesStr[i]);

			avFriendMarkerPic = (ImageView) avFriendMarkerLayout
					.findViewById(R.id.friend_marker_pic);
			squaredFriendBmp = createCenteredSquaredImage(imgsBmp[i]);

			friendMaskBmp = BitmapFactory.decodeResource(resources,
					R.drawable.av_mask);
			scaledFriendMaskBmp = Bitmap.createScaledBitmap(friendMaskBmp,
					squaredFriendBmp.getWidth(), squaredFriendBmp.getHeight(),
					true);

			friendResultBmp = Bitmap.createBitmap(squaredFriendBmp.getHeight(),
					squaredFriendBmp.getWidth(), Config.ARGB_8888);

			friendCanvas = new Canvas(friendResultBmp);
			friendPaint = new Paint();
			// Set Transfer Mode (cookie cutter style)
			friendPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.DST_IN));
			friendCanvas.drawBitmap(squaredFriendBmp, 0, 0, null);
			friendCanvas.drawBitmap(scaledFriendMaskBmp, 0, 0, friendPaint);
			// Reset Transfer Mode
			friendPaint.setXfermode(null);

			avFriendMarkerPic.setImageBitmap(friendResultBmp);

			friendMarkerBmp = createDrawableFromView(this, avFriendMarkerLayout);

			// Friend Marker
			if (Double.parseDouble(latitudesStr[i]) == 0.0
					&& Double.parseDouble(longitudesStr[i]) == 0.0) {
				friendsWithoutCoordinates = true;
			} else {
				Log.d(TAG, "LATITUDE: " + Double.parseDouble(latitudesStr[i]));
				map.addMarker(
						new MarkerOptions()
								.anchor((float) 0.5, (float) 0.5)
								.position(
										new LatLng(
												Double.parseDouble(latitudesStr[i]),
												Double.parseDouble(longitudesStr[i])))
								.title(namesStr[i])
								// .snippet(idsStr[i])
								.icon(BitmapDescriptorFactory
										.fromBitmap(friendMarkerBmp)))
						.setDraggable(true);
			}
		}

		// -------------------------------------

		// ----------------------
		// User marker with mask
		// ----------------------
		// userMarkerPic = (ImageView) userMarkerLayout
		// .findViewById(R.id.user_marker_pic);
		squaredUserBmp = createCenteredSquaredImage(imgsBmp[nFriends]);

		// userMaskBmp = BitmapFactory.decodeResource(resources,
		// R.drawable.custom_marker_mask);
		// scaledUserMaskBmp = Bitmap.createScaledBitmap(userMaskBmp,
		// squaredUserBmp.getWidth(), squaredUserBmp.getHeight(), true);
		//
		// userResultBmp = Bitmap.createBitmap(squaredUserBmp.getHeight(),
		// squaredUserBmp.getWidth(), Config.ARGB_8888);
		//
		// userCanvas = new Canvas(userResultBmp);
		// userPaint = new Paint();
		// // Set Transfer Mode (cookie cutter style)
		// userPaint.setXfermode(new
		// PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		// userCanvas.drawBitmap(squaredUserBmp, 0, 0, null);
		// userCanvas.drawBitmap(scaledUserMaskBmp, 0, 0, userPaint);
		// // Reset Transfer Mode
		// userPaint.setXfermode(null);
		//
		// userMarkerPic.setImageBitmap(userResultBmp);
		// ----------------

		// userMarkerBmp = createDrawableFromView(this, userMarkerLayout);
		userMarkerBmp = Bitmap
				.createScaledBitmap(BitmapFactory.decodeResource(resources,
						R.drawable.user_marker), 35, 50, true);
		// User Marker
		map.addMarker(new MarkerOptions().anchor((float) 0.5, (float) 0.5)
				.position(userLocation).title(namesStr[nFriends])
				.snippet(idsStr[nFriends])
				.icon(BitmapDescriptorFactory.fromBitmap(userMarkerBmp)));

		// User image on top menu
		userTopImage = (ImageView) mapView.findViewById(R.id.photo);
		userTopImage.setImageBitmap(squaredUserBmp);

		// User name on top menu
		userTopName = (TextView) mapView.findViewById(R.id.username);
		userTopName.setText(namesStr[nFriends]);

		// Move the camera instantly with a zoom of 17.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

		if (friendsWithoutCoordinates) {
			Log.d(TAG, "Some friends cannot be seen on map view!");
			Toast toast = Toast.makeText(this.getApplicationContext(),
					"Some friends cannot be seen on map view!",
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	private Bitmap createCenteredSquaredImage(Bitmap tmpBmp) {
		if (tmpBmp == null) {
			// Log.d(TAG, "Imagem vazia...");
			return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		} else {
			// Log.d(TAG, "Imagem carregada.");
			if (tmpBmp.getHeight() > tmpBmp.getWidth()) {
				int startY = (int) ((tmpBmp.getHeight() - tmpBmp.getWidth()) / 2);
				return Bitmap.createBitmap(tmpBmp, 0, startY,
						tmpBmp.getWidth(), tmpBmp.getWidth());
			} else {
				int startX = (int) ((tmpBmp.getWidth() - tmpBmp.getHeight()) / 2);
				return Bitmap.createBitmap(tmpBmp, startX, 0,
						tmpBmp.getHeight(), tmpBmp.getHeight());
			}
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {
		Log.d("longclick", "criou um bondpoint!");
		if (marker == false) {
			bp = Bitmap.createScaledBitmap(
					BitmapFactory.decodeResource(resources, R.drawable.add_bp),
					100, 100, true);

			map.addMarker(
					new MarkerOptions().anchor((float) 0.5, (float) 0.5)
							.position(point).title(bp_title)
							.icon(BitmapDescriptorFactory.fromBitmap(bp)))
					.setDraggable(true);
			marker = true;
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Convert a view to bitmap
	public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels,
				displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
				view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isClosed()) {
			Intent intent = new Intent(this.getApplicationContext(),
					MainActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		String title = marker.getTitle();
		if (title.equals(bp_title)) {
			// abre atividade
			startActivity(bpIntent);
		}

		return false;
	}

}

class MyInfoWindowAdapter implements InfoWindowAdapter {

	private final View infoView;

	MyInfoWindowAdapter(Activity mainActivity) {
		infoView = mainActivity.getLayoutInflater().inflate(
				R.layout.info_window, null);
	}

	@Override
	public View getInfoContents(Marker marker) {
		TextView name = (TextView) infoView.findViewById(R.id.friendName);

		name.setText(marker.getTitle());
		name.setBackgroundColor(infoView.getResources().getColor(
				R.color.bp_white));
		name.setTextColor(infoView.getResources()
				.getColor(R.color.bp_dark_grey));

		return infoView;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

}