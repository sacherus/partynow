package com.sacherus.partynow.activities;

import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.text.InputType;
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
import com.sacherus.partynow.provider.PartynowContracts;
import com.sacherus.partynow.provider.PartynowContracts.PartyColumnHelper;
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
	private AlarmManagerBroadcastReceiver alarm;

	private EditText kmsEditText;
	private Intent intent;
	private static String[] projection = { PartyColumnHelper.TITLE, PartyColumnHelper.DESCRIPTION_NAME,
			PartyColumnHelper.START, PartyColumnHelper._ID };
	private Button participantsButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parties);

		String[] uiBindFrom = projection;
		int[] uiBindTo = { R.id.title, R.id.description, R.id.time };
		mAdapter = new SimpleCursorAdapter(this, R.layout.party_list_item, null, uiBindFrom, uiBindTo, 0);
		listView = (ListView) findViewById(R.id.partiesListView);
		listView.setAdapter(mAdapter);

		refreshButton = (Button) findViewById(R.id.refreshAllPartiesButton);
		// setListAdapter(mAdapter);
		areaPartiesButton = (Button) findViewById(R.id.partiesInYourAreaButton);
		kmsEditText = (EditText) findViewById(R.id.kmsEditText);

		loadermanager = getLoaderManager();
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
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor c = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
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

		// TODO: MAKE THIS BETTER WAY
		RestApi.i().getUsers();
		// alarm = new AlarmManagerBroadcastReceiver();
		// startRepeating();

	}

	final int CONTEXT_DISPLAY = 1;
	final int CONTEXT_EDIT = 2;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
			// intent.putExtra("type", true);
			startActivity(intent);
			break;
		}

		return super.onContextItemSelected(item);
	}

	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String projection[] = SimplePartyNowContentProvider.partyProjectionMap.keySet().toArray(new String[0]);
		cursorLoader = new CursorLoader(this, PartynowContracts.PartyColumnHelper.CONTENT_URI, projection, null, null,
				null);
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
		getMenuInflater().inflate(R.menu.parties, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_logout) {
			Utils.log("logout");
			RestApi.i().logout();
		}
		if (item.getItemId() == R.id.km) {

			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				int num = 0;

				public void onClick(DialogInterface dialog, int id) {
					try {
						num = Integer.parseInt(input.getText().toString());
					} catch (NumberFormatException nfe) {
						System.out.println("Could not parse " + nfe);
					}
					RestApi.i().setKms(num);
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			alert.setTitle("Enter new distance:");
			alert.show();
		}
		if (item.getItemId() == R.id.interval) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			final EditText input = new EditText(this);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);
			alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				int num = 0;

				public void onClick(DialogInterface dialog, int id) {
					try {
						num = Integer.parseInt(input.getText().toString());
					} catch (NumberFormatException nfe) {
						System.out.println("Could not parse " + nfe);
					}
					RestApi.i().setInterval(num);
				}
			});
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
			alert.setTitle("Enter new update time:");
			alert.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private void startRepeating() {
		Context context = this.getApplicationContext();
		if (alarm != null) {
			alarm.SetAlarm(context);
		}
	}

}