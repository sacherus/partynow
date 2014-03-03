package com.sacherus.partynow.activities;

import com.sacherus.partynow.R;
import com.sacherus.partynow.R.layout;
import com.sacherus.partynow.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public abstract class BaseActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

}
