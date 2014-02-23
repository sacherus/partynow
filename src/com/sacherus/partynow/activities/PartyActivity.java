package com.sacherus.partynow.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gpstracking.GPSTracker;
import com.sacherus.partynow.R;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.pojos.Party.PartyBuilder;
import com.sacherus.partynow.provider.PartiesContract;

public class PartyActivity extends Activity {

	private Button confirmButton;
	private Button cancelButton;
	private Button joinButton;
	private Button clearButton;
	
	private TextView longitudeTextView;
	private TextView latitudeTextView;
	private EditText partyTitleEditText;
	private EditText startEdit;
	private EditText endEdit;
	private EditText descriptionEdit;
	private EditText participantsEdit;
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
		setContentView(R.layout.activity_party);

		partyTitleEditText = (EditText) findViewById(R.id.partyTitleEditText);
		longitudeTextView = (TextView) findViewById(R.id.addPartyLongitude);
		latitudeTextView = (TextView) findViewById(R.id.addPartyLatitude);
		startEdit = (EditText) findViewById(R.id.startDate);
		descriptionEdit = (EditText) findViewById(R.id.description);
//		participantsEdit = (EditText) findViewById(R.id.startDate);
		endEdit = (EditText) findViewById(R.id.startDate);
		
		confirmButton = (Button) findViewById(R.id.confirm);
		clearButton = (Button) findViewById(R.id.clear);
		cancelButton = (Button) findViewById(R.id.cancel);
		joinButton = (Button) findViewById(R.id.join);
		
		Party party;
		if (getIntent().hasExtra(PARTY)) {
			Bundle bundle = getIntent().getExtras();
			party = (Party) bundle.getSerializable(PARTY);
			displayParty(party);
		} else {
			setGps();
			Party.PartyBuilder pb = new Party.PartyBuilder();
			pb.addLatitude(longitude).addLongitude(longitude).addTitle("Test title");
			party = pb.build();
		}

		displayParty(party);

		confirmButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				insertToLocal();
				finish();
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

	private void displayParty(Party party) {
		startEdit.setText(party.getStart());
		partyTitleEditText.setText(party.getTitle());
		descriptionEdit.setText(party.getDescription());
		longitudeTextView.setText(Double.toString(party.getLongitude()));
		latitudeTextView.setText(Double.toString(party.getLatitude()));
	}

	public Party readPartyObject() {
		String partyTitle = partyTitleEditText.getText().toString();
		Party party = new Party();
		party.setTitle(partyTitle);
		double latitude = Double.parseDouble(latitudeTextView.getText().toString());
		party.setLatitude(latitude);
		double longitude = Double.parseDouble(longitudeTextView.getText().toString());
		party.setLongitude(longitude);
		clearForm();
		return party;
	}

	public void clearForm() {
		partyTitleEditText.setText("");
	}

	void insertToLocal() {
		Party party = readPartyObject();
		getContentResolver().insert(PartiesContract.PartyColumnHelper.PARTIES_URI_REST, party.toContent());
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_party, menu);
		return true;
	}
}
