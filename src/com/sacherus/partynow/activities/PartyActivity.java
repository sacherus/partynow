package com.sacherus.partynow.activities;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.gpstracking.GPSTracker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.sacherus.partynow.R;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.provider.PartynowContracts;
import com.sacherus.partynow.provider.SimplePartyNowContentProvider;
import com.sacherus.partynow.provider.PartynowContracts.UserColumnHelper;
import com.sacherus.partynow.rest.RestApi;
import com.sacherus.utils.Utils;

public class PartyActivity extends Activity {

	public static String LOCATION = "location";
	public static String ADDRESS = "address";
	public static int REQUEST_CODE = 1;
	
	private Party incomingParty;

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
	private Button showOnMap;
	private Button participantsButton;

	private TextView longitudeTextView;
	private TextView latitudeTextView;
	private EditText partyTitleEditText;
	private EditText descriptionEdit;
	private TextView participants;
	private TextView organizedby;

	private DatePicker startDate;
	private TimePicker startTime;
	private DatePicker endDate;
	private TimePicker endTime;
	private Gson gson;
	// private EditText Edit;
	// private EditText Edit;
	// private EditText Edit;

	private GPSTracker gps;

	private double latitude;
	private double longitude;

	public static String PARTY = "party";
	public static String TYPE = "type";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().hasExtra(TYPE)) {
			prepareNonEdit();
		} else {
			prepareEdit();
		}
		gson = RestApi.i().getGson();
	}

	private void prepareEdit() {
		setContentView(R.layout.activity_party);

		partyTitleEditText = (EditText) findViewById(R.id.partyTitleEditText);
		longitudeTextView = (TextView) findViewById(R.id.addPartyLongitude);
		latitudeTextView = (TextView) findViewById(R.id.addPartyLatitude);
		descriptionEdit = (EditText) findViewById(R.id.description);
		organizedby = (TextView) findViewById(R.id.organizedby);
		participants = (TextView) findViewById(R.id.participants);

		startDate = (DatePicker) findViewById(R.id.start_datepicker);
		startTime = (TimePicker) findViewById(R.id.start_timePicker);
		endDate = (DatePicker) findViewById(R.id.end_datepicker);
		endTime = (TimePicker) findViewById(R.id.end_timePicker);

		confirmButton = (Button) findViewById(R.id.confirm);
		clearButton = (Button) findViewById(R.id.clear);
		cancelButton = (Button) findViewById(R.id.cancel);
		showOnMap = (Button) findViewById(R.id.showonmap);
		participantsButton = (Button) findViewById(R.id.participantsButton);

		if (getIntent().hasExtra(PARTY)) {
			// VIEW PARTY
			Bundle bundle = getIntent().getExtras();
			incomingParty = (Party) bundle.getSerializable(PARTY);
		} else {
			// NEW PARTY
			setGps();
			Party.PartyBuilder pb = new Party.PartyBuilder();
			pb.addLatitude(latitude).addLongitude(longitude).addTitle("Your party title")
					.addStartDate(Utils.dateToString(new java.util.Date()))
					.addEndDate(Utils.dateToString(new java.util.Date()));
			incomingParty = pb.build();
		}

		Utils.log("Editing id" + incomingParty.getId());
		displayParty(incomingParty);

		confirmButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!checkDates()) {
					RestApi.i().longMsg("End date should be after start date!");
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

		showOnMap.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				showOnMap();
			}

		});

		participantsButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (incomingParty != null)
					showUsers((ArrayList<Integer>) incomingParty.getParticipants());
				else
					showUsers(null);
			}
		});
	}

	private void showUsers(ArrayList<Integer> users) {
		Intent i = new Intent(this, UsersActivity.class);
		i.putIntegerArrayListExtra(UsersActivity.USERS, users);
		startActivity(i);
	}

	private void showOnMap() {
		//TODO: what is it?

		Party party = readPartyObject();
		Intent intent = new Intent(PartyActivity.this, MapActivity.class);
		intent.putExtra(MapActivity.PARTY, party);
		startActivityForResult(intent, REQUEST_CODE);
	}

	private void prepareNonEdit() {
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
		sb.append("Place: ").append(String.valueOf(party.getLongitude())).append(" ")
				.append(String.valueOf(party.getLatitude()));
	}
	


	private void displayParty(Party party) {
		// startEdit.setText(party.getStart());
		partyTitleEditText.setText(party.getTitle());
		descriptionEdit.setText(party.getDescription());
		longitudeTextView.setText(Double.toString(party.getLongitude()));
		latitudeTextView.setText(Double.toString(party.getLatitude()));
		startDate.init(party.getStartYear(), party.getStartMonth(), party.getStartDay(), null);
		endDate.init(party.getEndYear(), party.getEndMonth(), party.getEndDay(), null);
		startDate.setMinDate(Utils.stringToDate(party.getStart()).getTime());
		endDate.setMinDate(Utils.stringToDate(party.getEnd()).getTime());
		participants.setText(party.getParticipants().toString());
		organizedby.setText(party.getOrganizers().toString());
	}

	/*
	 * TODO: USE BUILDER
	 */
	public Party readPartyObject() {
		String partyTitle = partyTitleEditText.getText().toString();
		double lon = Double.parseDouble("" + longitudeTextView.getText());
		double lat = Double.parseDouble("" + latitudeTextView.getText());
		Party party;
		Party.PartyBuilder pb = new Party.PartyBuilder();
		pb.addTitle(partyTitle).addLatitude(lat).addLongitude(lon).addStartDate(getStartDateFromDatePicker())
				.addEndDate(getEndDateFromDatePicker()).addId(incomingParty.getId());
		party = pb.build();
		Utils.log("Editing party id: " + party.getId());
		return party;
	}

	public void clearForm() {
		partyTitleEditText.setText("");
		descriptionEdit.setText("");
		startDate.setMinDate((new java.util.Date()).getTime());
		endDate.setMinDate((new java.util.Date()).getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		startDate.init(cal.YEAR, cal.MONTH, cal.DAY_OF_MONTH, null);
		endDate.init(cal.YEAR, cal.MONTH, cal.DAY_OF_MONTH, null);
	}

	void insertToLocal() {
		Party party = readPartyObject();
		if (isEdition()) {
			// getContentResolver().insert(PartiesContract.PartyColumnHelper.PARTIES_URI_REST,
			// party.toContent());
			// TODO: change to content provider
			RestApi.i().editParty(party);
		} else {
			getContentResolver().insert(PartynowContracts.PartyColumnHelper.PARTIES_URI_REST, party.toContent());
		}
	}

	public void setGps() {
		gps = new GPSTracker(PartyActivity.this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			longitudeTextView.setText(Double.toString(longitude));
			latitudeTextView.setText(Double.toString(latitude));
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}
	}

	private String getStartDateFromDatePicker() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(startDate.getYear())).append("-");
		sb.append(String.valueOf(startDate.getMonth() + 1)).append("-");
		sb.append(String.valueOf(startDate.getDayOfMonth())).append("T");
		sb.append(String.valueOf(startTime.getCurrentHour())).append(":");
		sb.append(String.valueOf(startTime.getCurrentMinute())).append(":00Z");
		return sb.toString();
	}

	private String getEndDateFromDatePicker() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(endDate.getYear())).append("-");
		sb.append(String.valueOf(endDate.getMonth() + 1)).append("-");
		sb.append(String.valueOf(endDate.getDayOfMonth())).append("T");
		sb.append(String.valueOf(endTime.getCurrentHour())).append(":");
		sb.append(String.valueOf(endTime.getCurrentMinute())).append(":00Z");
		return sb.toString();

	}

	private boolean checkDates() {
		Calendar cal = Calendar.getInstance();
		cal.set(startDate.getYear(), startDate.getMonth(), startDate.getDayOfMonth(), startTime.getCurrentHour(),
				startTime.getCurrentMinute());
		Calendar cal2 = Calendar.getInstance();
		cal2.set(endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(), endTime.getCurrentHour(),
				endTime.getCurrentMinute());
		return cal2.after(cal);
	}

	private boolean isEdition() {
		return incomingParty.getId() > 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.parties, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_logout) {
			RestApi.i().logout();
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PartyActivity.REQUEST_CODE) {
			 LatLng position = (LatLng) data.getParcelableExtra(LOCATION);
			 String address = data.getStringExtra(ADDRESS);
			 longitudeTextView.setText("" + position.longitude);
			 latitudeTextView.setText("" + position.latitude);
			 RestApi.i().longMsg("Latitude and longitude successfuly set");
		}		
	}

}
