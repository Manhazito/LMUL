package org.feup.bondpoint;

// PARA O LONGCLICK !!!
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class Map extends MapActivity {
	private MyCustomMapView mapView;

	private View longPressMarker = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map);
		mapView = (MyCustomMapView) findViewById(R.id.mapview);

		mapView.setOnLongpressListener(new MyCustomMapView.OnLongpressListener() {
			public void onLongpress(final MapView view,
					final GeoPoint longpressLocation) {
				runOnUiThread(new Runnable() {
					public void run() {

						// So para testar..
						longPressMarker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
								.inflate(R.layout.av_friend_marker, null);

					}
				});
			}
		});
	}
}
