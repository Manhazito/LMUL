package org.feup.bondpoint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

	private String[] friendNames = null;
	private String[] friendIds = null;
	private Bitmap[] friendImgsBmp = null;
	private byte[][] friendImgsBmpByteArray = null;

	private ReceiveFriends receiveFriends = null;
	private static final String TAG = "MapActivity";
	private static final int BP_RESPONSE = 300;
	private static final int FRIEND_RESPONSE = 400;

	private static String bp_title = "New BondPoint";

	private BondPoint[] bondPointArray = null;
	private BondPoint newBondPoint = null;
	private int nBP = 0;
	private Marker newBondPointMarker = null;
	private Intent bpIntent = null;
	private Intent friendIntent = null;

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

	private View avFriendMarkerLayout = null;
	private ImageView avFriendMarkerPic = null;

	private int nFriends = 0;
	private String label = "";
	private LatLng userLocation = null;

	private Bitmap friendMaskBmp = null;
	private Bitmap bp = null;
	private Bitmap scaledFriendMaskBmp = null;
	private Bitmap userMarkerBmp = null;
	private Bitmap friendMarkerBmp = null;
	private Bitmap squaredUserBmp = null;
	private Bitmap squaredFriendBmp = null;
	private Canvas friendCanvas = null;
	private Paint friendPaint = null;
	private Bitmap friendResultBmp = null;

	private Button logoutBtn = null;
	private ImageView userTopImage = null;
	private TextView userTopName = null;

	private byte[][] imgsBmpByteArray = null;

	private Boolean friendsWithoutCoordinates = false;

	private Session session;

	private String requestId;

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
		friendIntent = new Intent(mapView.getContext(),
				ListFriendsActivity.class);

		logoutBtn = (Button) mapView.findViewById(R.id.logout);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setInfoWindowAdapter(new MyInfoWindowAdapter(this));

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

		loadBondPoints();

		namesStr = mapIntent.getStringArrayExtra("names");
		idsStr = mapIntent.getStringArrayExtra("ids");
		latitudesStr = mapIntent.getStringArrayExtra("latitudes");
		longitudesStr = mapIntent.getStringArrayExtra("longitudes");

		nFriends = namesStr.length - 1;
		label = "";

		imgsBmpByteArray = new byte[nFriends + 1][];
		imgsBmp = new Bitmap[nFriends + 1];

		for (int i = 0; i <= nFriends; i++) {
			label = "picture" + i;
			imgsBmpByteArray[i] = mapIntent.getByteArrayExtra(label);
			if (imgsBmpByteArray[i] != null) {
				imgsBmp[i] = BitmapFactory.decodeByteArray(imgsBmpByteArray[i],
						0, imgsBmpByteArray[i].length);
			} else {
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
		// -------------------------------------
		friendsWithoutCoordinates = false;
		friendMarkerBmp = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(resources, R.drawable.av), 50, 50,
				true);
		for (int i = 0; i < nFriends; i++) {

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
				map.addMarker(
						new MarkerOptions()
								.anchor((float) 0.5, (float) 0.5)
								.position(
										new LatLng(
												Double.parseDouble(latitudesStr[i]),
												Double.parseDouble(longitudesStr[i])))
								.title(namesStr[i])
								.icon(BitmapDescriptorFactory
										.fromBitmap(friendMarkerBmp)))
						.setDraggable(true);
			}
		}

		// ----------------------
		// User marker with mask
		// ----------------------
		squaredUserBmp = createCenteredSquaredImage(imgsBmp[nFriends]);

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

		// Move the camera instantly with a zoom of 9.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 9));

		// Zoom in, animating the camera.
		map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);

		if (friendsWithoutCoordinates) {
			// Log.d(TAG, "Some friends cannot be seen on map view!");
			// Toast toast = Toast.makeText(this.getApplicationContext(),
			// "Some friends cannot be seen on map view!",
			// Toast.LENGTH_LONG);
			// toast.show();
		}
	}

	private Bitmap createCenteredSquaredImage(Bitmap tmpBmp) {
		if (tmpBmp == null) {
			return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		} else {
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
		Log.d("longclick", "criou um bondpoint marker!");

		if (newBondPoint != null) {
			newBondPoint.getMarker().remove();
			newBondPoint = null;
		}

		newBondPoint = new BondPoint();

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		sharedPreferences.edit().remove("NameBP").commit();
		sharedPreferences.edit().remove("TypeBP").commit();
		sharedPreferences.edit().remove("DescriptionBP").commit();
		sharedPreferences.edit().remove("InitDateTimeBP").commit();
		sharedPreferences.edit().remove("EndDateTimeBP").commit();

		bp = Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(resources, R.drawable.add_bp),
				100, 100, true);

		newBondPointMarker = map.addMarker(new MarkerOptions()
				.anchor((float) 0.5, (float) 0.5).position(point)
				.title(bp_title).icon(BitmapDescriptorFactory.fromBitmap(bp)));
		newBondPointMarker.setSnippet("");
		newBondPointMarker.setDraggable(true);
		newBondPoint.setMarker(newBondPointMarker);
	}

	@Override
	public void onMapClick(LatLng point) {
		if (newBondPointMarker != null)
			newBondPointMarker.remove();
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
		// Check if the user is authenticated and
		// an incoming notification needs handling
		if (state.isOpened() && requestId != null) {
			Toast.makeText(MapActivity.this.getApplicationContext(),
					"Incoming request", Toast.LENGTH_LONG).show();
			requestId = null;
		}
		if (state.isClosed()) {
			Intent intent = new Intent(this.getApplicationContext(),
					MainActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		BondPoint bp = getBondPoint(marker.getId());

		String title = marker.getTitle();
		// devia usar o marker id...
		if (title.equals(bp_title)) {
			// abre atividade para criar bond point
			startActivityForResult(bpIntent, BP_RESPONSE);
		} else {
			// if (bp != null) {
			// Abre atividade para convidar amigos
			// startActivityForResult(friendIntent, FRIEND_RESPONSE);
			// Log.i("TESTE", "Chegou aqui!");

			// if (receiveFriends == null) {
			// receiveFriends = new ReceiveFriends();
			// receiveFriends.execute(this);
			// }
		}

		return false;
	}

	private BondPoint getBondPoint(String idStr) {
		if (bondPointArray == null)
			return null;

		int nElements = bondPointArray.length;

		for (int i = 0; i < nElements; i++) {
			if (bondPointArray[i].getId() == idStr)
				return bondPointArray[i];
		}

		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (BP_RESPONSE): {
			if (resultCode == Activity.RESULT_OK) {
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(this);

				newBondPointMarker.remove();

				String eventId = sharedPreferences.getString("IdEv", "");

				if (eventId.equals("")) {
					Log.d(TAG, "Evento Facebook não foi criado!");
					break;
				}

				BondPoint disposableBondPoint = new BondPoint();
				disposableBondPoint.setName(sharedPreferences.getString(
						"NameBP", "Name of Your Bond Point"));
				sharedPreferences.edit().remove("NameBP").commit();

				disposableBondPoint.setType(sharedPreferences.getString(
						"TypeBP", "Type of Your Bond Point"));
				sharedPreferences.edit().remove("TypeBP").commit();

				disposableBondPoint.setDescription(sharedPreferences.getString(
						"DescriptionBP", "Description of BP"));
				sharedPreferences.edit().remove("DescriptionBP").commit();

				disposableBondPoint.setStartTime(sharedPreferences.getString(
						"InitDateTimeBP", "Initial Date and Time of your BP"));
				sharedPreferences.edit().remove("InitDateTimeBP").commit();

				disposableBondPoint.setEndTime(sharedPreferences.getString(
						"EndDateTimeBP", "End Date and Time of BP"));
				sharedPreferences.edit().remove("EndDateTimeBP").commit();

				disposableBondPoint.setEventId(eventId);
				sharedPreferences.edit().remove("IdEv").commit();

				// Muda Imagem do Marker!
				bp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
						resources, R.drawable.add_to_bp), 100, 100, true);

				Marker disposableBondPointMarker = map
						.addMarker(new MarkerOptions()
								.anchor((float) 0.5, (float) 0.5)
								.position(
										newBondPoint.getMarker().getPosition())
								.title(disposableBondPoint.getName())
								.icon(BitmapDescriptorFactory.fromBitmap(bp)));
				disposableBondPointMarker.setSnippet("");
				disposableBondPointMarker.setDraggable(false);
				disposableBondPoint.setMarker(disposableBondPointMarker);

				// Obriga a actualizar o nome na InfoWindow
				disposableBondPoint.getMarker().hideInfoWindow();
				disposableBondPoint.getMarker().showInfoWindow();

				// Save BondPoint to System File
				String folderName = "BondPoints";
				String fileName = "BondPoint_" + nBP + ".data";

				File path = new File(getFilesDir(), folderName);
				if (!path.exists())
					path.mkdir();

				File file = new File(path, fileName);
				if (!file.exists()) {
					Log.d(TAG, "File doesn't exist.");
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(
								file, true));

						bw.write(disposableBondPoint.getName());
						bw.newLine();
						bw.write(disposableBondPoint.getType());
						bw.newLine();
						bw.write(disposableBondPoint.getDescription());
						bw.newLine();
						bw.write(disposableBondPoint.getId());
						bw.newLine();
						bw.write(disposableBondPoint.getStartTime());
						bw.newLine();
						bw.write(disposableBondPoint.getEndTime());
						bw.newLine();
						bw.write(disposableBondPoint.getMarker().getTitle());
						bw.newLine();
						bw.write(disposableBondPoint.getMarker().getSnippet());
						bw.newLine();
						bw.write(Double.toString(disposableBondPoint
								.getMarker().getPosition().latitude));
						bw.newLine();
						bw.write(Double.toString(disposableBondPoint
								.getMarker().getPosition().longitude));
						bw.newLine();
						bw.write(disposableBondPoint.getEventId());
						bw.flush();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					Log.d(TAG, "File already exists: ");

				// Reads saved Bond Point
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(file));

					String line = br.readLine();
					Log.d(TAG, "Nome: " + line);
					line = br.readLine();
					Log.d(TAG, "Tipo: " + line);
					line = br.readLine();
					Log.d(TAG, "Descrição: " + line);
					line = br.readLine();
					Log.d(TAG, "ID: " + line);
					line = br.readLine();
					Log.d(TAG, "Data início: " + line);
					line = br.readLine();
					Log.d(TAG, "Data fim: " + line);
					line = br.readLine();
					Log.d(TAG, "Título: " + line);
					line = br.readLine();
					Log.d(TAG, "Snippet: " + line);
					line = br.readLine();
					Log.d(TAG, "Latitude: " + line);
					line = br.readLine();
					Log.d(TAG, "Longitude: " + line);
					line = br.readLine();
					Log.d(TAG, "ID do evento: " + line);
					br.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				nBP++;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				newBondPoint.getMarker().remove();
			}
			break;
		}
		}
	}

	private void loadBondPoints() {
		String folderName = "BondPoints";
		File path = new File(getFilesDir(), folderName);
		if (!path.exists()) {
			Log.d(TAG, "Pasta " + path + " não existe...");
			return; // Não há BondPoints guardados!
		}

		int nFiles = path.listFiles().length;
		int filesLoaded = 0;
		int fileIndex = 0;
		bondPointArray = new BondPoint[nFiles];
		Log.d(TAG, "Existem " + nFiles + " ficheiros guardados.");
		while (filesLoaded < nFiles) {
			String fileName = "BondPoint_" + fileIndex + ".data";

			File file = new File(path, fileName);
			if (!file.exists()) {
				Log.d(TAG, "File do not exists");
				fileIndex++;
				continue;
			} else {
				Log.d(TAG, "BondPoint file exists: ");
				bondPointArray[filesLoaded] = new BondPoint();
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(file));

					String nome = br.readLine();
					if (nome == null)
						nome = "";
					Log.d(TAG, "Nome: " + nome);

					String tipo = br.readLine();
					if (tipo == null)
						tipo = "";
					Log.d(TAG, "Tipo: " + tipo);

					String desc = br.readLine();
					if (desc == null)
						desc = "";
					Log.d(TAG, "Descrição: " + desc);

					String id = br.readLine();
					if (id == null)
						id = "";
					Log.d(TAG, "ID: " + id);

					String ini = br.readLine();
					if (ini == null)
						ini = "";
					Log.d(TAG, "Data início: " + ini);

					String end = br.readLine();
					if (end == null)
						end = "";
					Log.d(TAG, "Data fim: " + end);

					String title = br.readLine();
					if (title == null)
						title = "";
					Log.d(TAG, "Título: " + title);

					String snip = br.readLine();
					if (snip == null)
						snip = "";
					Log.d(TAG, "Snippet: " + snip);

					String latitude = br.readLine();
					if (latitude == null)
						latitude = "0.0";
					Double lat = Double.parseDouble(latitude);
					Log.d(TAG, "Latitude: " + lat);

					String longitude = br.readLine();
					if (longitude == null)
						longitude = "0.0";
					Double lon = Double.parseDouble(longitude);
					Log.d(TAG, "Longitude: " + lon);

					String evId = br.readLine();
					if (evId == null)
						evId = "";
					Log.d(TAG, "Event ID: " + evId);

					br.close();

					LatLng location = new LatLng(lat, lon);

					// criar Marker
					bp = Bitmap.createScaledBitmap(BitmapFactory
							.decodeResource(resources, R.drawable.add_to_bp),
							100, 100, true);

					Marker mkr = map.addMarker(new MarkerOptions()
							.anchor((float) 0.5, (float) 0.5)
							.position(location).title(title).snippet(snip)
							.icon(BitmapDescriptorFactory.fromBitmap(bp)));
					mkr.setDraggable(false);

					bondPointArray[filesLoaded].setMarker(mkr);
					bondPointArray[filesLoaded].setName(nome);
					bondPointArray[filesLoaded].setType(tipo);
					bondPointArray[filesLoaded].setDescription(desc);
					bondPointArray[filesLoaded].setId(id);
					bondPointArray[filesLoaded].setStartTime(ini);
					bondPointArray[filesLoaded].setEndTime(end);
					bondPointArray[filesLoaded].setEventId(evId);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				filesLoaded++;
				fileIndex++;
				// file.delete(); // Para fazer RESET
			}
		}
		nBP = filesLoaded;
	}

	public void setFriendNames(String[] names) {
		this.friendNames = names;
	}

	public void setFriendIds(String[] ids) {
		this.friendIds = ids;
	}

	public void setByteArray(Bitmap[] imgsBmp) {
		this.friendImgsBmp = imgsBmp;
	}

	public void setnFriends(int nFriends) {
		this.nFriends = nFriends;
		this.friendNames = new String[nFriends];
		this.friendIds = new String[nFriends];
		this.friendImgsBmpByteArray = new byte[nFriends][];
	}

	public void showList() {
		friendIntent.putExtra("names", friendNames);
		friendIntent.putExtra("id", friendIds);
		for (int i = 0; i < nFriends; i++) {
			label = "picture" + i;
			friendIntent.putExtra(label, imgsBmpByteArray[i]);
		}

		startActivityForResult(friendIntent, FRIEND_RESPONSE);
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
