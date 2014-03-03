package com.sacherus.partynow.activities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sacherus.partynow.R;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.rest.RestApi;

public class MapActivity extends Activity {
	private LatLng myposition = new LatLng(51.0, 51.0);
	private GoogleMap map;
	public static String PARTY = "party";
	// TODO: show multiple parties
	public static String PARTIES = "parties";
	private Geocoder geoCoder;
	private Marker partyMarker;
	private String address;
	private Button setLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		geoCoder = new Geocoder(this, Locale.getDefault());
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		setLocation = (Button) findViewById(R.id.setLocation);
		
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				RestApi.i().longMsg(arg0.toString());
				partyMarker.remove();
				myposition = arg0;
				MarkerOptions mo = new MarkerOptions().position(myposition);
				partyMarker = map.addMarker(mo);
			}
		});

		setLocation.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseLocationOnMap();
			}
		});

		Bundle bundle = getIntent().getExtras();
		if (getIntent().hasExtra(PARTY)) {
			Party party = (Party) bundle.getSerializable(PartyActivity.PARTY);
			partyMarker = map.addMarker(partyToMarker(party));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(myposition, 15));
			map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
			try {
				RestApi.i().longMsg(getMyLocationAddress(myposition));
			} catch (IOException e) {
				e.printStackTrace();
				RestApi.i().longMsg(e.toString());
			}
		} else if (getIntent().hasExtra(PARTIES)) {
		}
	}

	private void chooseLocationOnMap() {
		String address;
		try {
			address = getMyLocationAddress(myposition);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			address = "Cannot get adress";
		}
		
		Intent intent = new Intent(this, PartyActivity.class);
		intent.putExtra(PartyActivity.LOCATION, myposition);
		intent.putExtra(PartyActivity.ADDRESS, address);
		setResult(PartyActivity.REQUEST_CODE, intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	private MarkerOptions partyToMarker(Party party) {
		myposition = new LatLng(party.getLatitude(), party.getLongitude());
		return (new MarkerOptions()).position(myposition).title(party.getTitle());
	}

	private String getMyLocationAddress(LatLng latLng) throws IOException {

		// Place your latitude and longitude
		List<Address> addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
		if (addresses != null) {
			Address fetchedAddress = addresses.get(0);
			StringBuilder strAddress = new StringBuilder();
			for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
				strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
			}
			return strAddress.toString();
		} else
			return "No location found..!";

	}

	private void reverseGeoCoding(String location) {
		try {
			List<Address> addresses = geoCoder.getFromLocationName(location, 1);
			String strCompleteAddress = "";
			if (addresses.size() > 0) {
				LatLng p = new LatLng((int) (addresses.get(0).getLatitude() * 1E6), (int) (addresses.get(0)
						.getLongitude() * 1E6));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
				map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
				// map.invalidate();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}