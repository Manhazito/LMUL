package org.feup.bondpoint;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class ConnectActivity extends Activity {

	private ProgressDialog progressDialog = null;

	// criacao das variaveis para depois referenciar
	Button btnInitDateTime, btnEndDateTime, btnSave, btnCancel, btnInvite;

	// criacao das variaveis de text para depois referenciar
	EditText textInitDateTime, textEndDateTime, textBPName, textBPType,
			textBPDescription;

	// criacao da variavel spinner para depois referenciar
	Spinner spinnerBPType;

	private Session session;

	private static final String TAG = "ConnectActivity";
	private static final List<String> PERMISSIONS = Arrays
			.asList("create_event");

	static final int INIT_DATE_TIME_ID = 100;
	static final int END_DATE_TIME_ID = 200;

	private int buttonClicked = -1;

	private String initDateTime = null;
	private String endDateTime = null;

	// variables to save user selected date and time
	public int year, month, day, hour, minute;

	private Intent fbIntent;

	public class FriendPickerApplication extends Application {
		private Collection<GraphUser> selectedUsers;

		public Collection<GraphUser> getSelectedUsers() {
			return selectedUsers;
		}

		public void setSelectedUsers(Collection<GraphUser> selectedUsers) {
			this.selectedUsers = selectedUsers;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bpcreation);

		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("Creating Event... Please Wait...");
		progressDialog.hide();

		// get the references of buttons
		btnInitDateTime = (Button) findViewById(R.id.bpIniDateTimeButton);
		btnEndDateTime = (Button) findViewById(R.id.bpEndDateTimeButton);
		btnSave = (Button) findViewById(R.id.bpSaveButton);
		btnCancel = (Button) findViewById(R.id.bpCancelButton);
		btnInvite = (Button) findViewById(R.id.bpInviteButton);

		// get the references of texts
		textInitDateTime = (EditText) findViewById(R.id.bpInitDateTimeText);
		textEndDateTime = (EditText) findViewById(R.id.bpEndDateTimeText);
		textBPName = (EditText) findViewById(R.id.bpNameText);
		textBPType = (EditText) findViewById(R.id.bpType);
		textBPDescription = (EditText) findViewById(R.id.bpDescriptionText);

		// get the references of spinner
		spinnerBPType = (Spinner) findViewById(R.id.bpTypeSpinner);

		// chamada do load
		loadSavedPreferences();

		// Set ClickListener on btnInitDateTime
		btnInitDateTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonClicked = INIT_DATE_TIME_ID;
				showDateTimeDialog();
			}
		});

		// Set ClickListener on btnEndDateTime
		btnEndDateTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				buttonClicked = END_DATE_TIME_ID;
				showDateTimeDialog();
			}
		});

		// Set ClickListener on btnSave
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// Mostrar mensagem ao utilizador...
				progressDialog.show();

				sendRequestDialog();
				// sendEvent(); // Comentar, se estiver a fazer requests!

				savePreferences("NameBP", textBPName.getText().toString());
				savePreferences("TypeBP", textBPType.getText().toString());
				savePreferences("DescriptionBP", textBPDescription.getText()
						.toString());
				savePreferences("InitDateTimeBP", textInitDateTime.getText()
						.toString());
				savePreferences("EndDateTimeBP", textEndDateTime.getText()
						.toString());

				setResult(Activity.RESULT_OK);

			}
		});

		// Set ClickListener on btnCancel
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		});

		// Set ClickListener on btnSave
		btnInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// fbIntent = new Intent();
				// fbIntent.setClass(getApplicationContext(),
				// PickFriendsActivity.class);
				// startActivity(fbIntent);
				fbIntent = new Intent();
				fbIntent.setClass(getApplicationContext(),
						PickFriendsActivity.class);
				startActivity(fbIntent);
			}
		});

	}

	private void goodBye() {
		progressDialog.dismiss();
		finish();
	}

	// Send facebook request
	private void sendRequestDialog() {
		Bundle params = new Bundle();
		params.putString("message", "Convite para novo BONDPOINT");

		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
				ConnectActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error != null) {
							if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(
										ConnectActivity.this
												.getApplicationContext(),
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
								finish();
							} else {
								Toast.makeText(
										ConnectActivity.this
												.getApplicationContext(),
										"Network Error", Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							final String requestId = values
									.getString("request");
							if (requestId != null) {
								Toast.makeText(
										ConnectActivity.this
												.getApplicationContext(),
										"Request sent", Toast.LENGTH_SHORT)
										.show();
								sendEvent();
							} else {
								Toast.makeText(
										ConnectActivity.this
												.getApplicationContext(),
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
								finish();
							}
						}
					}

				}).build();
		requestsDialog.show();
	}

	private void sendEvent() {
		Log.i(TAG, "A tentar criar evento...");
		session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				// pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
			}

			Bundle bundle = new Bundle();
			bundle.putString("name", textBPName.getText().toString());
			bundle.putString("start_time", initDateTime);
			bundle.putString("end_time", endDateTime);
			bundle.putString("description", textBPDescription.getText()
					.toString());
			bundle.putString("privacy_type", "SECRET");
			bundle.putString("message", "My message");
			Request postRequest = new Request(Session.getActiveSession(),
					"me/events", bundle, HttpMethod.POST, new Callback() {
						@Override
						public void onCompleted(Response response) {
							String eventId = "";
							try {
								eventId = response.getGraphObject()
										.getInnerJSONObject().get("id")
										.toString();
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (java.lang.NullPointerException e) {
								e.printStackTrace();
							}
							savePreferences("IdEv", eventId);

							goodBye();
						}
					});
			postRequest.executeAsync();
		} else {
			Toast.makeText(ConnectActivity.this.getApplicationContext(),
					"You are not logged in on Facebook.", Toast.LENGTH_LONG)
					.show();
			goodBye();
		}
	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	// funções fixes
	private void loadSavedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String BPname = sharedPreferences.getString("NameBP",
				"Name of your Bond Point");
		String BPtype = sharedPreferences.getString("TypeBP",
				"Type of your Bond Point");
		String BPDescription = sharedPreferences.getString("DescriptionBP",
				"Description of your Bond Point");
		String BPInitDateTime = sharedPreferences.getString("InitDateTimeBP",
				"Initial date of your Bond Point");
		String BPEndDateTime = sharedPreferences.getString("EndDateTimeBP",
				"End date of your Bond Point");

		textBPName.setHint(BPname);
		textBPType.setHint(BPtype);
		textBPDescription.setHint(BPDescription);
		textInitDateTime.setHint(BPInitDateTime);
		textEndDateTime.setHint(BPEndDateTime);
	}

	private void savePreferences(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// acabam as funções fixes

	private void showDateTimeDialog() {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
				.findViewById(R.id.DateTimePicker);
		// Check if system is set to use 24h time
		final boolean is24h = DateFormat.is24HourFormat(this);

		// Do updates when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mDateTimePicker.clearFocus();

						String ampmStr = "";

						year = mDateTimePicker.get(Calendar.YEAR);
						month = mDateTimePicker.get(Calendar.MONTH) + 1;
						day = mDateTimePicker.get(Calendar.DAY_OF_MONTH);
						if (mDateTimePicker.is24HourView()) {
							hour = mDateTimePicker.get(Calendar.HOUR_OF_DAY);
							minute = mDateTimePicker.get(Calendar.MINUTE);
						} else {
							hour = mDateTimePicker.get(Calendar.HOUR);
							minute = mDateTimePicker.get(Calendar.MINUTE);
							ampmStr = (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM) ? " AM"
									: " PM";
						}

						if (buttonClicked == INIT_DATE_TIME_ID) {
							// Set the Selected Date in Select date Button
							textInitDateTime.setText("Initial date: " + year
									+ "-" + month + "-" + day + "T" + hour
									+ ":" + minute + "Z" + ampmStr);
							initDateTime = year + "-" + month + "-" + day + "T"
									+ hour + ":" + minute + "Z";
						} else if (buttonClicked == END_DATE_TIME_ID) {
							// Set the Selected Date in Select date Button
							textEndDateTime.setText("End date: " + year + "-"
									+ month + "-" + day + "T" + hour + ":"
									+ minute + "Z" + ampmStr);
							endDateTime = year + "-" + month + "-" + day + "T"
									+ hour + ":" + minute + "Z";
						}

						buttonClicked = -1;
						mDateTimeDialog.dismiss();
					}
				});

		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mDateTimeDialog.cancel();
					}
				});

		// Reset Date and Time pickers when the "Reset" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mDateTimePicker.reset();
					}
				});

		// Setup TimePicker
		mDateTimePicker.setIs24HourView(is24h);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

}
