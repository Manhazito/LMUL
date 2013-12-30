package org.feup.bondpoint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.facebook.FacebookException;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

public class PickFriendsActivity extends FragmentActivity {
	FriendPickerFragment friendPickerFragment;

	// A helper to simplify life for callers who want to populate a Bundle with
	// the necessary
	// parameters. A more sophisticated Activity might define its own set of
	// parameters; our needs
	// are simple, so we just populate what we want to pass to the
	// FriendPickerFragment.
	public static void populateParameters(Intent intent, String userId,
			boolean multiSelect, boolean showTitleBar) {
		intent.putExtra(FriendPickerFragment.USER_ID_BUNDLE_KEY, userId);
		intent.putExtra(FriendPickerFragment.MULTI_SELECT_BUNDLE_KEY,
				multiSelect);
		intent.putExtra(FriendPickerFragment.SHOW_TITLE_BAR_BUNDLE_KEY,
				showTitleBar);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pick_friends_activity);

		FragmentManager fm = getSupportFragmentManager();

		if (savedInstanceState == null) {
			// First time through, we create our fragment programmatically.
			final Bundle args = getIntent().getExtras();
			friendPickerFragment = new FriendPickerFragment(args);
			fm.beginTransaction()
					.add(R.id.friend_picker_fragment, friendPickerFragment)
					.commit();
		} else {
			// Subsequent times, our fragment is recreated by the framework and
			// already has saved and
			// restored its state, so we don't need to specify args again. (In
			// fact, this might be
			// incorrect if the fragment was modified programmatically since it
			// was created.)
			friendPickerFragment = (FriendPickerFragment) fm
					.findFragmentById(R.id.friend_picker_fragment);
		}

		friendPickerFragment
				.setOnErrorListener(new PickerFragment.OnErrorListener() {
					@Override
					public void onError(PickerFragment<?> fragment,
							FacebookException error) {
						PickFriendsActivity.this.onError(error);
					}
				});

		friendPickerFragment
				.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
					@Override
					public void onDoneButtonClicked(PickerFragment<?> fragment) {
						// We just store our selection in the Application for
						// other activities to look at.
						FriendPickerApplication application = (FriendPickerApplication) getApplication();
						application.setSelectedUsers(friendPickerFragment
								.getSelection());

						setResult(RESULT_OK, null);
						finish();
					}
				});
	}

	private void onError(Exception error) {
		// String text = getString(R.string.exception, error.getMessage());
		// Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		// toast.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			// Load data, unless a query has already taken place.
			friendPickerFragment.loadData(false);
		} catch (Exception ex) {
			onError(ex);
		}
	}
}
