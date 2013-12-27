package org.feup.bondpoint;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

	private MainFragment mainFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Typeface Regular = Typeface.createFromAsset(getAssets(),
		// "fonts/roboto_regular.ttf");
		// Typeface Medium = Typeface.createFromAsset(getAssets(),
		// "fonts/roboto_medium.ttf");
		// Typeface Bold = Typeface.createFromAsset(getAssets(),
		// "fonts/roboto_bold.ttf");
		// Typeface Light = Typeface.createFromAsset(getAssets(),
		// "fonts/roboto_light.ttf");

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
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu., menu);
	// return true;
	// }

}