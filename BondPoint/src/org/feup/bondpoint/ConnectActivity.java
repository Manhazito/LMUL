package org.feup.bondpoint;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class ConnectActivity extends Activity {
	// criacao das variaveis para depois referenciar

	Button btnSelectDate, btnSelectTime, btnSelectTime2;

	// criacao das variaveis de text para depois referenciar

	EditText textDate, textStart, textEnd;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	static final int TIME_DIALOG_ID2 = 2;

	// variables to save user selected date and time
	public int year, month, day, hour, minute, hour2, minute2;
	// declare the variables to Show/Set the date and time when Time and Date
	// Picker Dialog first appears
	private int mYear, mMonth, mDay, mHour, mMinute;

	// constructor

	public ConnectActivity() {
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

		// teste para ver se o texto da data vai para as caixas devidas

		textDate = (EditText) findViewById(R.id.DatBP);
		textStart = (EditText) findViewById(R.id.StartBP);
		textEnd = (EditText) findViewById(R.id.EndBP);

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

		// Set ClickListener on btnSelectTime
		btnSelectTime2.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				// Show the TimePickerDialog
				showDialog(TIME_DIALOG_ID2);
			}
		});

	}

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
			textStart.setText("START :" + hour + "-" + minute);
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
			textEnd.setText("END :" + hour2 + "-" + minute2);
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
