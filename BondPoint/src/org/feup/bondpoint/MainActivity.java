package org.feup.bondpoint;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class MainActivity extends FragmentActivity {

	private MainFragment mainFragment;
	private String requestId;
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			mainFragment = new MainFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, mainFragment).commit();
		} else {
			// Or set the fragment from restored state info
			mainFragment = (MainFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}

		// Check for an incoming notification. Save the info
		Uri intentUri = MainActivity.this.getIntent().getData();
		Log.d("intentUri", "Valor:" + intentUri);
		if (intentUri != null) {
			String requestIdParam = intentUri.getQueryParameter("request_ids");
			if (requestIdParam != null) {
				String array[] = requestIdParam.split(",");
				requestId = array[0];
				Log.i(TAG, "Request id: " + requestId);
			}
		}
	}
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu., menu);
	// return true;
	// }

}