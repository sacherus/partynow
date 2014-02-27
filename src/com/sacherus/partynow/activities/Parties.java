package com.sacherus.partynow.activities;

import java.io.Serializable;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.sacherus.partynow.R;
import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.provider.PartiesContract;
import com.sacherus.partynow.provider.PartiesContract.PartyColumnHelper;
import com.sacherus.partynow.provider.SimplePartyNowContentProvider;
import com.sacherus.partynow.rest.RestApi;
import com.sacherus.utils.Utils;

public class Parties extends Activity implements LoaderCallbacks<Cursor> {

	SimpleCursorAdapter mAdapter;
	LoaderManager loadermanager;
	CursorLoader cursorLoader;
	private static String TAG = "CursorLoader";
	private Button addPartyButton;
	private ListView listView;
	private Button refreshButton;
	private Button areaPartiesButton;
	private EditText kmsEditText;
	private Intent intent;
	private static String[] projection = { PartyColumnHelper.TITLE, PartyColumnHelper.DESCRIPTION_NAME, PartyColumnHelper.START, PartyColumnHelper._ID};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parties);
		loadermanager = getLoaderManager();
 
		String[] uiBindFrom = projection;
		int[] uiBindTo = { R.id.title, R.id.description, R.id.time};

		/* Empty adapter that is used to display the loaded data */
		mAdapter = new SimpleCursorAdapter(this, R.layout.party_list_item, null, uiBindFrom, uiBindTo, 0);
		listView = (ListView) findViewById(R.id.partiesListView);
		listView.setAdapter(mAdapter);
		refreshButton = (Button) findViewById(R.id.refreshAllPartiesButton);
//		setListAdapter(mAdapter);
		areaPartiesButton = (Button) findViewById(R.id.partiesInYourAreaButton);
		kmsEditText = (EditText) findViewById(R.id.kmsEditText);
		/**
		 * This initializes the loader and makes it active. If the loader
		 * specified by the ID already exists, the last created loader is
		 * reused. If the loader specified by the ID does not exist,
		 * initLoader() triggers the LoaderManager.LoaderCallbacks method
		 * onCreateLoader(). This is where you implement the code to instantiate
		 * and return a new loader. Use restartLoader() instead of this, to
		 * discard the old data and restart the Loader. Hence, here the given
		 * LoaderManager.LoaderCallbacks implementation are associated with the
		 * loader.
		 */
		loadermanager.initLoader(1, null, this);
		
		/** End of loader **/
		
		addPartyButton = (Button) findViewById(R.id.addPartyButton);
		addPartyButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Parties.this, PartyActivity.class);
				startActivity(intent);
			}
		}); 
		
		refreshButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				RestApi.i().getParties();
			}
		});
		
		areaPartiesButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				RestApi.i().getPartiesInArea(Double.parseDouble(kmsEditText.getText().toString()));
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view,
	                int position, long id) {
	        	Cursor c = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
	        	c.moveToPosition(position);
	        	intent = new Intent(view.getContext(), PartyActivity.class);
	            ContentValues cv = new ContentValues();
	            DatabaseUtils.cursorRowToContentValues(c, cv); 
	            Party party = Party.fromContent(cv);
	            Utils.log(party.getOrganizers().toString());
	            intent.putExtra(PartyActivity.PARTY, (Serializable) party);
	            if (RestApi.i().isCurrentUserOrganizor(party)) {
	            registerForContextMenu(listView);
                openContextMenu(listView);
	            } else {
	            startActivity(intent);
	            }
	        }
	    });
	}

	final int CONTEXT_DISPLAY = 1;
	final int CONTEXT_EDIT = 2;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		// Context menu
		menu.setHeaderTitle("");
		menu.add(Menu.NONE, CONTEXT_DISPLAY, Menu.NONE, "Display");
		menu.add(Menu.NONE, CONTEXT_EDIT, Menu.NONE, "Edit");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CONTEXT_DISPLAY: 
			intent.putExtra("type", true);
			startActivity(intent);
			break;
		case CONTEXT_EDIT: 
			//intent.putExtra("type", true);
			startActivity(intent);
			break;
		}

		return super.onContextItemSelected(item);
	}

	/**
	 * This creates and return a new Loader (CursorLoader or custom Loader) for
	 * the given ID. This method returns the Loader that is created, but you
	 * don't need to capture a reference to it.
	 */
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		/**
		 * This requires the URI of the Content Provider projection is the list
		 * of columns of the database to return. Null will return all the
		 * columns selection is the filter which declares which rows to return.
		 * Null will return all the rows for the given URI. selectionArgs: You
		 * may include ?s in the selection, which will be replaced by the values
		 * from selectionArgs, in the order that they appear in the selection.
		 * The values will be bound as Strings. sortOrder determines the order
		 * of rows. Passing null will use the default sort order, which may be
		 * unordered. To back a ListView with a Cursor, the cursor must contain
		 * a column named _ID.
		 */
		
		String projection[] = SimplePartyNowContentProvider.partyProjectionMap.keySet().toArray(new String[0]);
		cursorLoader = new CursorLoader(this, PartiesContract.PartyColumnHelper.CONTENT_URI, projection, null, null,
				null);
		return cursorLoader;
	}

	/**
	 * Called when a previously created loader has finished its load. This
	 * assigns the new Cursor but does not close the previous one. This allows
	 * the system to keep track of the Cursor and manage it for us, optimizing
	 * where appropriate. This method is guaranteed to be called prior to the
	 * release of the last data that was supplied for this loader. At this point
	 * you should remove all use of the old data (since it will be released
	 * soon), but should not do your own release of the data since its loader
	 * owns it and will take care of that. The framework would take of closing
	 * of old cursor once we return.
	 */

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (mAdapter != null && cursor != null)
			mAdapter.swapCursor(cursor); // swap the new cursor in.
		else
			Log.v(TAG, "OnLoadFinished: mAdapter is null");
	}

	/**
	 * This method is triggered when the loader is being reset and the loader
	 * data is no longer available. This is called when the last Cursor provided
	 * to onLoadFinished() above is about to be closed. We need to make sure we
	 * are no longer using it.
	 */

	public void onLoaderReset(Loader<Cursor> arg0) {
		if (mAdapter != null)
			mAdapter.swapCursor(null);
		else
			Log.v(TAG, "OnLoadFinished: mAdapter is null");
	}
	

	

}