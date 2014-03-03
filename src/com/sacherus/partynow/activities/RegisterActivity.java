package com.sacherus.partynow.activities;

import java.io.IOException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sacherus.partynow.R;
import com.sacherus.partynow.pojos.User;
import com.sacherus.partynow.rest.RestApi;

public class RegisterActivity extends Activity {

	private Button registerNewAccountButton;
	private EditText passwordEdit;
	private EditText fullnameEdit;
	private EditText emailEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
		registerNewAccountButton = (Button) findViewById(R.id.registerButton);
		emailEdit = (EditText) findViewById(R.id.regEmailEdit);
		fullnameEdit = (EditText) findViewById(R.id.regFullnameEdit);
		passwordEdit = (EditText) findViewById(R.id.regPasswordEdit);

		// Listening to Login Screen link
		loginScreen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Closing registration screen
				// Switching to Login Screen/closing register screen
				finish();
			}
		});

		registerNewAccountButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				RegisterTask rt = new RegisterTask();
				rt.execute();
			}
		});
	}

	private User getUser() {
		User user = new User(fullnameEdit.getText().toString(), passwordEdit.getText().toString(), emailEdit.getText().toString());
		return user;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	public class RegisterTask extends AsyncTask<Void, Void, Boolean> {
		Exception ex;
		final static String REGISTRATION_SUCCESS = "Registartion succesful";

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				RestApi.i().register(getUser());
			} catch (IOException e) {
				ex = e;
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success)
				Toast.makeText(RegisterActivity.this.getApplicationContext(), REGISTRATION_SUCCESS, Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(RegisterActivity.this.getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
		}

		// @Override
		// protected void onCancelled() {
		// mAuthTask = null;
		// showProgress(false);
		// }
	}
}
