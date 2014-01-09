package org.feup.bondpoint;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
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
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class ConnectActivity extends Activity {

	// criacao das variaveis para depois referenciar
	Button btnInitDateTime, btnEndDateTime, btnSave, btnCancel, btnInvite;

	// criacao das variaveis de text para depois referenciar
	EditText textInitDateTime, textEndDateTime, textEndDateHour, textBPName,
			textBPType, textBPDescription;

	// criacao da variavel spinner para depois referenciar
	Spinner spinnerBPType;

	private Session session;

	private static final String TAG = "ConnectActivity";
	private static final List<String> PERMISSIONS = Arrays
			.asList("create_event");

	static final int INIT_DATE_TIME_ID = 100;
	static final int END_DATE_TIME_ID = 200;

	private int buttonClicked = -1;

	// variables to save user selected date and time
	public int year, month, day, hour, minute;

	// Picker Dialog first appears
	// private int mYear, mMonth, mDay, mHour, mMinute;

	// private Bondpoint bondP;

	private Intent fbIntent;

	// constructor
	public ConnectActivity() {
		// Assign current Date and Time Values to Variables
		// final Calendar calendar = Calendar.getInstance();
		// mYear = calendar.get(Calendar.YEAR);
		// mMonth = calendar.get(Calendar.MONTH);
		// mDay = calendar.get(Calendar.DAY_OF_MONTH);
		// mHour = calendar.get(Calendar.HOUR_OF_DAY);
		// mMinute = calendar.get(Calendar.MINUTE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bpcreation);

		// get the references of buttons
		btnInitDateTime = (Button) findViewById(R.id.bpIniDateTimeButton);
		btnEndDateTime = (Button) findViewById(R.id.bpEndDateTimeButton);
		btnSave = (Button) findViewById(R.id.bpSaveButton);
		btnCancel = (Button) findViewById(R.id.bpCancelButton);
		btnInvite = (Button) findViewById(R.id.bpInviteButton);

		// get the references of texts
		textInitDateTime = (EditText) findViewById(R.id.bpInitDateTimeText);
		textEndDateTime = (EditText) findViewById(R.id.bpEndDateTimeText);
		textEndDateHour = (EditText) findViewById(R.id.bpEndDateTimeText);
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

				savePreferences("NameBP", textBPName.getText().toString());
				savePreferences("TypeBP", textBPType.getText().toString());
				savePreferences("DescriptionBP", textBPDescription.getText()
						.toString());
				savePreferences("InitDateTimeBP", textInitDateTime.getText()
						.toString());
				savePreferences("EndDateTimeBP", textEndDateTime.getText()
						.toString());

				setResult(Activity.RESULT_OK);

				// sendRequestDialog();
				sendEvent();
				// marcador = nome do BondPoint

				// finish();
			}
		});

		// Set ClickListener on btnCancel
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// savePreferences("NameBP", textName.getText().toString());
				// savePreferences("TypeBP", textType.getText().toString());
				// savePreferences("BPDescr", textDescr.getText().toString());
				// savePreferences("DateBP", textDate.getText().toString());
				// savePreferences("StartBP", textStart.getText().toString());
				// savePreferences("EndBP", textEnd.getText().toString());

				finish();
			}
		});

		// Set ClickListener on btnSave
		btnInvite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fbIntent = new Intent();
				fbIntent.setClass(getApplicationContext(),
						PickFriendsActivity.class);
				startActivity(fbIntent);
			}
		});

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
			// session.close();
			// session.openForPublish(openRequest)
			// try {
			// Session.OpenRequest request = new Session.OpenRequest(this);
			// request.setPermissions(Arrays.asList("create_event"));
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
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
			bundle.putString("start_time", textInitDateTime.getText()
					.toString() + "-" + textEndDateHour.getText().toString());
			// bundle.putString("end_time",
			// textEndDateTime.getText().toString());
			// bundle.putString("start_time", "11-1-2014T1:23:30-23:55");
			// bundle.putString("end_time",
			// textEndDateTime.getText().toString());
			bundle.putString("description", textBPDescription.getText()
					.toString());
			bundle.putString("privacy_type", "SECRET");
			bundle.putString("message", "My message");
			Request postRequest = new Request(Session.getActiveSession(),
					"me/events", bundle, HttpMethod.POST, new Callback() {
						@Override
						public void onCompleted(Response response) {
							Log.i(TAG, response.toString());
						}
					});
			postRequest.executeAsync();
			finish();
		} else {
			Toast.makeText(ConnectActivity.this.getApplicationContext(),
					"You are not logged in on Facebook.", Toast.LENGTH_LONG)
					.show();
			finish();
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
							textInitDateTime.setText(day + "-" + month + "-"
									+ year + "T1:" + hour + ":" + minute
									+ ampmStr);
						} else if (buttonClicked == END_DATE_TIME_ID) {
							// Set the Selected Date in Select date Button
							textEndDateTime.setText(day + "-" + month + "-"
									+ year);
							textEndDateHour.setText(hour + ":" + minute
									+ ampmStr);
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
