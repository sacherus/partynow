package com.sacherus.partynow.activities;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.gpstracking.GPSTracker;
import com.sacherus.partynow.R;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.provider.PartiesContract;
import com.sacherus.partynow.rest.RestApi;
import com.sacherus.utils.Utils;

public class PartyActivity extends Activity {

	// display
	private TextView title;
	private TextView description;
	private TextView dates;
	private TextView place;
	private Button joinButton;
	
	// edit
	private Button confirmButton;
	private Button cancelButton;
	private Button clearButton;

	private TextView longitudeTextView;
	private TextView latitudeTextView;
	private EditText partyTitleEditText;
	private EditText descriptionEdit;
	private EditText participantsEdit;
	private DatePicker startDate;
	private TimePicker startTime;
	private DatePicker endDate;
	private TimePicker endTime;
	// private EditText Edit;
	// private EditText Edit;
	// private EditText Edit;

	private GPSTracker gps;

	private double latitude;
	private double longitude;

	public static String PARTY = "party";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = new Bundle();
		if (getIntent().hasExtra("type")) {
			Bundle bundle = getIntent().getExtras();
		final Party party = (Party) bundle.getSerializable(PARTY);
		setContentView(R.layout.activity_display);
		title = (TextView) findViewById(R.id.title_d);
		description = (TextView) findViewById(R.id.desc_d);
		dates = (TextView) findViewById(R.id.dates_d);
		place = (TextView) findViewById(R.id.place_d);
		joinButton = (Button) findViewById(R.id.join_d);
		joinButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RestApi.i().join(party.getId());			
			}
		});
		
		title.setText(party.getTitle());
		description.setText(party.getDescription());
		StringBuilder sb = new StringBuilder();
		sb.append("Date: ").append(party.getStart()).append(" - ").append(party.getEnd());
		dates.setText(sb.toString());
		sb.setLength(0);
		sb.append("Place: ").append(String.valueOf(party.getLongitude())).append(" ").append(String.valueOf(party.getLatitude()));
		} else {
		setContentView(R.layout.activity_party);
		
		partyTitleEditText = (EditText) findViewById(R.id.partyTitleEditText);
		longitudeTextView = (TextView) findViewById(R.id.addPartyLongitude);
		latitudeTextView = (TextView) findViewById(R.id.addPartyLatitude);
		descriptionEdit = (EditText) findViewById(R.id.description);

		startDate = (DatePicker) findViewById(R.id.start_datepicker);
		startTime = (TimePicker) findViewById(R.id.start_timePicker);
		endDate = (DatePicker) findViewById(R.id.end_datepicker);
		endTime = (TimePicker) findViewById(R.id.end_timePicker);

		// participantsEdit = (EditText) findViewById(R.id.startDate);

		confirmButton = (Button) findViewById(R.id.confirm);
		clearButton = (Button) findViewById(R.id.clear);
		cancelButton = (Button) findViewById(R.id.cancel);
		joinButton = (Button) findViewById(R.id.join);

		Party party;
		// VIEW PARTY
		if (getIntent().hasExtra(PARTY)) {
			Bundle bundle = getIntent().getExtras();
			party = (Party) bundle.getSerializable(PARTY);
			displayParty(party);
			// NEW PARTY
		} else {
			Calendar cal = Calendar.getInstance();  
			cal.set(cal.YEAR, cal.MONTH, cal.DAY_OF_MONTH);
			cal.set(Calendar.HOUR_OF_DAY, cal.HOUR_OF_DAY);
		    cal.set(Calendar.MINUTE, cal.MINUTE);
		    Log.i("lol", "min "+cal.MINUTE+cal.HOUR);
		    startDate.setMinDate(cal.getTimeInMillis());
			
			Log.i("lol","add2");
			setGps();
			Party.PartyBuilder pb = new Party.PartyBuilder();
			pb.addLatitude(longitude).addLongitude(longitude)
					.addTitle("Test title").addStartDate(Utils.dateToString(new java.util.Date()))
					.addEndDate(Utils.dateToString(new java.util.Date()));
			party = pb.build();
			startDate.setMinDate(Utils.stringToDate(party.getStart()).getTime());
			endDate.setMinDate(Utils.stringToDate(party.getEnd()).getTime());
		}
		// sprawdzic ilosc cyfer w month i przykladowy wyswietlic lol
		displayParty(party);

		confirmButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!checkDates()) {
					RestApi.i().msg("End date should be after start date!");
					endDate.requestFocus();
				} else {
				insertToLocal();
				finish();
				}
			}
		});

		cancelButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		clearButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				clearForm();

			}
		});
		}
	}

	private void displayParty(Party party) {
		// startEdit.setText(party.getStart());
		partyTitleEditText.setText(party.getTitle());
		descriptionEdit.setText(party.getDescription());
		longitudeTextView.setText(Double.toString(party.getLongitude()));
		latitudeTextView.setText(Double.toString(party.getLatitude()));
		startDate.init(party.getStartYear(), party.getStartMonth(),
				party.getStartDay(), null);
		endDate.init(party.getEndYear(), party.getEndMonth(),
				party.getEndDay(), null);
	}

	/*
	 * TODO: USE BUILDER
	 */
	public Party readPartyObject() {
		String partyTitle = partyTitleEditText.getText().toString();
		double latitude = Double.parseDouble(latitudeTextView.getText()
				.toString());
		double longitude = Double.parseDouble(longitudeTextView.getText()
				.toString());

		Party party;
		Party.PartyBuilder pb = new Party.PartyBuilder();
		pb.addTitle(partyTitle).addLatitude(latitude).addLongitude(longitude)
				.addStartDate(getStartDateFromDatePicker())
				.addEndDate(getEndDateFromDatePicker());
		party = pb.build();
		clearForm();
		return party;
	}

	public void clearForm() {
		partyTitleEditText.setText("");
	}

	void insertToLocal() {
		Party party = readPartyObject();
		getContentResolver().insert(
				PartiesContract.PartyColumnHelper.PARTIES_URI_REST,
				party.toContent());
	}

	public void setGps() {
		gps = new GPSTracker(PartyActivity.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			longitudeTextView.setText(Double.toString(longitude));
			latitudeTextView.setText(Double.toString(latitude));
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}
	}// jeszcze w tej grupie jest i Martynka i Pocha... ja nie wiem czy to
		// zniose...

	private String getStartDateFromDatePicker() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(startDate.getYear())).append("-");
		sb.append(String.valueOf(startDate.getMonth())).append("-");
		sb.append(String.valueOf(startDate.getDayOfMonth())).append("'T'");
		sb.append(String.valueOf(startTime.getCurrentHour())).append(":");
		sb.append(String.valueOf(startTime.getCurrentMinute())).append(":00'Z'");
		return sb.toString();
	}

	private String getEndDateFromDatePicker() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(endDate.getYear())).append("-");
		sb.append(String.valueOf(endDate.getMonth())).append("-");
		sb.append(String.valueOf(endDate.getDayOfMonth())).append("'T'");
		sb.append(String.valueOf(endTime.getCurrentHour())).append(":");
		sb.append(String.valueOf(endTime.getCurrentMinute())).append(":00'Z'");
		return sb.toString();
	}

	private boolean checkDates(){
		Calendar cal = Calendar.getInstance();
		cal.set(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth());
		Calendar cal2 = Calendar.getInstance();
		cal2.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth());
		return cal2.after(cal);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_party, menu);
		return true;
	}
}
