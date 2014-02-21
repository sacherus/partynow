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
import com.sacherus.partynow.provider.PartiesContract;

public class AddPartyActivity extends Activity {
	EditText partyTitleEditText;
	Button sendButton;
	TextView longitudeTextView;
	TextView latitudeTextView;
	GPSTracker gps;
	double latitude;
	double longitude;
	private Button refreshButton;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_party);

		partyTitleEditText = (EditText) findViewById(R.id.partyTitleEditText);
		sendButton = (Button) findViewById(R.id.sendPartyToServerButton);
		longitudeTextView = (TextView) findViewById(R.id.addPartyLongitude);
		latitudeTextView = (TextView) findViewById(R.id.addPartyLatitude);

		
		
		partyTitleEditText.setText("Test party");
		setGps();
		sendButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				setGps();
				insertToLocal();
			}
		});
		
		
	}

	public Party readPartyObject() {
		String partyTitle = partyTitleEditText.getText().toString();
		Party party = new Party();
		party.setTitle(partyTitle);
		double latitude = Double.parseDouble(latitudeTextView.getText().toString());
		party.setLatitude(latitude);
		double longitude = Double.parseDouble(longitudeTextView.getText().toString());
		party.setLongitude(longitude);
		cleanForm();
		return party;
	}

	public void cleanForm() {
		partyTitleEditText.setText("");
	}

	void insertToLocal() {
		Party party = readPartyObject();
		getContentResolver().insert(PartiesContract.PartyColumnHelper.PARTIES_URI_REST, party.toContent());
	}

	public void setGps() {
		gps = new GPSTracker(AddPartyActivity.this);
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
