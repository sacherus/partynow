package com.sacherus.partynow.activities;

import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.sacherus.partynow.R;
import com.sacherus.partynow.provider.PartynowContracts.UserColumnHelper;
import com.sacherus.utils.Utils;

public class UsersActivity extends Activity implements LoaderCallbacks<Cursor> {
	private String TAG = PartyActivity.class.getName();
	private SimpleCursorAdapter mAdapter;
	private LoaderManager loadermanager;
	private CursorLoader cursorLoader;
	public static String USERS = "users";
	private String where;
	private final String projection[] = { UserColumnHelper.USERNAME, UserColumnHelper.EMAIL, UserColumnHelper._ID   };
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		
		listView = (ListView) findViewById(R.id.usersListView);

		List<Integer> users = getIntent().getIntegerArrayListExtra(USERS);
		
		String[] uiBindFrom = projection;
		int[] uiBindTo = { R.id.username, R.id.email };
		mAdapter = new SimpleCursorAdapter(this, R.layout.user_list_item, null, uiBindFrom, uiBindTo, 0);
		listView.setAdapter(mAdapter);
		
		String commaUsers = users.toString().replace("[", "").replace("]", "");
		where = UserColumnHelper._ID + " in (" + commaUsers + ")";
		
		loadermanager = getLoaderManager();
		loadermanager.initLoader(1, null, this);
	}
	
	private void buildWhereClause() {

	}

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		cursorLoader = new CursorLoader(this, UserColumnHelper.URI, projection, where, null, null);
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (mAdapter != null && cursor != null)
			mAdapter.swapCursor(cursor); // swap the new cursor in.
		else
			Log.v(TAG, "OnLoadFinished: mAdapter is null");
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		if (mAdapter != null)
			mAdapter.swapCursor(null);
		else
			Log.v(TAG, "OnLoadFinished: mAdapter is null");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
