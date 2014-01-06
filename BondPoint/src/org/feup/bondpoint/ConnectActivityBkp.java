package org.feup.bondpoint;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class ConnectActivityBkp extends Activity {

	// criacao das variaveis para depois referenciar

	Button btnSelectDate, btnSelectTime, btnSelectTime2, btnSave;

	// criacao das variaveis de text para depois referenciar

	EditText textDate, textStart, textEnd, textName, textType, textDescr;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	static final int TIME_DIALOG_ID2 = 2;

	// variables to save user selected date and time
	public int year, month, day, hour, minute, hour2, minute2;
	// declare the variables to Show/Set the date and time when Time and Date
	// Picker Dialog first appears
	private int mYear, mMonth, mDay, mHour, mMinute;

	// constructor

	public ConnectActivityBkp() {
		// Assign current Date and Time Values to Variables
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bpcreation);

		// get the references of buttons
		btnSelectDate = (Button) findViewById(R.id.datebutton);
		btnSelectTime = (Button) findViewById(R.id.Startbutton);
		btnSelectTime2 = (Button) findViewById(R.id.Endbutton);
		btnSave = (Button) findViewById(R.id.savebutton);

		// teste para ver se o texto da data vai para as caixas devidas

		textDate = (EditText) findViewById(R.id.DatBP);
		textStart = (EditText) findViewById(R.id.StartBP);
		textEnd = (EditText) findViewById(R.id.EndBP);

		textName = (EditText) findViewById(R.id.editText1);
		textType = (EditText) findViewById(R.id.TypeBP);
		textDescr = (EditText) findViewById(R.id.descriptionBP);

		// chamada do load
		loadSavedPreferences();

		// Set ClickListener on btnSelectDate
		btnSelectDate.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// Show the DatePickerDialog
				showDialog(DATE_DIALOG_ID);
			}
		});

		// Set ClickListener on btnSelectTime
		btnSelectTime.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// Show the TimePickerDialog
				showDialog(TIME_DIALOG_ID);
			}
		});

		// Set ClickListener on btnSelectTime2
		btnSelectTime2.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// Show the TimePickerDialog
				showDialog(TIME_DIALOG_ID2);
			}
		});

		// para guardar a informa�ao
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ir pa outro sitio

				savePreferences("NameBP", textName.getText().toString());
				savePreferences("TypeBP", textType.getText().toString());
				savePreferences("BPDescr", textDescr.getText().toString());
				savePreferences("DateBP", textDate.getText().toString());
				savePreferences("StartBP", textStart.getText().toString());
				savePreferences("EndBP", textEnd.getText().toString());

				// NÃO FUNCIONA!!! :-(
				// sets destas variaveis no objecto bondP
				// set marker = nome bondpoint

				// tentar assim...
				setResult(Activity.RESULT_OK);
				finish();
			}
		});

	}

	// funcoes fixes

	private void loadSavedPreferences() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String BPname = sharedPreferences.getString("NameBP",
				"Name of Your Bond Point");
		String BPtype = sharedPreferences.getString("TypeBP",
				"Type of Your Bond Point");
		String DescrBP = sharedPreferences.getString("BPDescr",
				"Description of BP");
		String BPdate = sharedPreferences
				.getString("DateBP", "Date of your BP");
		String BPstart = sharedPreferences.getString("StartBP", "Start Time");
		String BPend = sharedPreferences.getString("EndBP", "End Time");
		textName.setText(BPname);
		textType.setText(BPtype);
		textDescr.setText(DescrBP);
		textDate.setText(BPdate);
		textStart.setText(BPstart);
		textEnd.setText(BPend);

	}

	private void savePreferences(String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	// acabam as funcoes fixes

	// Register DatePickerDialog listener
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// the callback received when the user "sets" the Date in the
		// DatePickerDialog
		public void onDateSet(DatePicker view, int yearSelected,
				int monthOfYear, int dayOfMonth) {
			year = yearSelected;
			month = monthOfYear;
			day = dayOfMonth;
			// Set the Selected Date in Select date Button
			// btnSelectDate.setText("Date selected : " + day + "-" + month +
			// "-"
			// + year);
			textDate.setText("Date selected : " + day + "-" + month + "-"
					+ year);
		}
	};

	// Register TimePickerDialog listener
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		// the callback received when the user "sets" the TimePickerDialog in
		// the dialog
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			hour = hourOfDay;
			minute = min;
			// Set the Selected Date in Select date Button
			// btnSelectTime.setText("START :" + hour + "-" + minute);
			textStart.setText("START : " + hour + ":" + minute);
		}
	};

	// Register TimePickerDialog listener
	private TimePickerDialog.OnTimeSetListener mTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
		// the callback received when the user "sets" the TimePickerDialog in
		// the dialog
		public void onTimeSet(TimePicker view, int hourOfDay, int min) {
			hour2 = hourOfDay;
			minute2 = min;
			// Set the Selected Date in Select date Button
			// btnSelectTime2.setText("END :" + hour + "-" + minute);
			textEnd.setText("END : " + hour2 + ":" + minute2);
		}
	};

	// Method automatically gets Called when you call showDialog() method
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// create a new DatePickerDialog with values you want to show
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
			// create a new TimePickerDialog with values you want to show
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					false);
		case TIME_DIALOG_ID2:
			return new TimePickerDialog(this, mTimeSetListener2, mHour,
					mMinute, false);

		}
		return null;
	}

}
