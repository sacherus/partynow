package com.sacherus.partynow.activities;

import java.io.IOException;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpstracking.GPSTracker;
import com.sacherus.partynow.R;
import com.sacherus.partynow.provider.PartynowContracts;
import com.sacherus.partynow.rest.RestApi;

public class TestActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	GPSTracker gps;
	private boolean autologin = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		Button buttonLogin = (Button) findViewById(R.id.loginButton);
		Button buttonTest = (Button) findViewById(R.id.button1);
		Button partiesMenu = (Button) findViewById(R.id.menuButton);
		Button mapsButton = (Button) findViewById(R.id.maps);
		buttonLogin.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TestActivity.this, GeoCoderActivity.class);
				startActivity(intent);
			}
		});
		
//		RestApi.i().init(MainActivity.this);
		
		partiesMenu.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
//				RestApi.i().shortMsg("autologin");
//				AutoLoginTask alt = new AutoLoginTask();
//				alt.execute();
//				RestApi.i().join(6);
//				RestApi.i().msg(RestApi.i().getMyUser().getUsername());
				Intent intent = new Intent(TestActivity.this, GeoCoderActivity.class);
				startActivity(intent);
			}
		});
		
		mapsButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(TestActivity.this, MapActivity.class);
				startActivity(intent);
			}
		});

		buttonTest.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// TwitterStatusesTask alt = new TwitterStatusesTask();
				// alt.execute();
				Intent intent = new Intent(TestActivity.this, Parties.class);
				startActivity(intent);
			}
		});

		if (autologin) {
			AutoLoginTask alt = new AutoLoginTask();
			alt.execute();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// params, ?, String
	class TwitterStatusesTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPostExecute(String result) {
			TextView textView = (TextView) findViewById(R.id.textView1);

			if (result == null) {
				textView.setText("Response failure");
			}
			{
				textView.setText(result);
			}

			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				return "Ala ma kota";
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return null;
		}
	}

	class AutoLoginTask extends AsyncTask<Void, Void, Boolean> {
		IOException ex;
		
		@Override
		protected Boolean doInBackground(Void... notype) {
			try {
				RestApi.i().getToken("sacherus", "a");
			} catch (IOException e) {
				ex = e;
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if(result) {
				RestApi.i().shortMsg("Test autologin successful");
			} else {
				RestApi.i().shortMsg(ex.toString());
			}	         
	     }

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { PartynowContracts.PartyColumnHelper._ID, PartynowContracts.PartyColumnHelper.TITLE };
		CursorLoader cursorLoader = new CursorLoader(this, PartynowContracts.PartyColumnHelper.CONTENT_URI, projection,
				null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Toast.makeText(this, "Loader started", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Toast.makeText(this, "Loader reseted", Toast.LENGTH_LONG).show();
	}

}
