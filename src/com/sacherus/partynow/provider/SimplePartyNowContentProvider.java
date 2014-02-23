package com.sacherus.partynow.provider;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.provider.PartiesContract.PartyColumnHelper;
import com.sacherus.partynow.rest.RestApi;

/**
 * Simple content provider that demonstrates the basics of creating a content
 * provider that stores basic video meta-data.
 */
public class SimplePartyNowContentProvider extends ContentProvider {

	private static final String TAG = SimplePartyNowContentProvider.class.getName();
	public static final String PARTY_TABLE_NAME = "parties";

	private static final int PARTIES = 1;
	private static final int PARTY_ID = 2;
	private static final int PARTY_REST = 3;

	public static final UriMatcher URI_MATCHER;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(PartiesContract.SIMPLE_AUTHORITY, PartiesContract.PartyColumnHelper.URI_PARTY_NAME, PARTIES);
		// use of the hash character indicates matching of an id
		URI_MATCHER.addURI(PartiesContract.SIMPLE_AUTHORITY, PartiesContract.PartyColumnHelper.URI_PARTY_NAME + "/#",
				PARTY_ID);
		URI_MATCHER.addURI(PartiesContract.SIMPLE_AUTHORITY, PartiesContract.PartyColumnHelper.URI_PARTY_NAME_REST,
				PARTY_REST);
	}

	private SimpleVideoDbHelper mOpenDbHelper;

	public static Map<String, String> partyProjectionMap;
	static {
		// example projection map, not actually used in this application
		partyProjectionMap = new TreeMap<String, String>();
		partyProjectionMap.put(PartyColumnHelper._ID, BaseColumns._ID);
		partyProjectionMap.put(PartyColumnHelper.TITLE, PartyColumnHelper.TITLE);
		partyProjectionMap.put(PartyColumnHelper.LATITUDE, PartyColumnHelper.LATITUDE);
		partyProjectionMap.put(PartyColumnHelper.LONGITUDE, PartyColumnHelper.LONGITUDE);
		partyProjectionMap.put(PartyColumnHelper.START, PartyColumnHelper.START);
		partyProjectionMap.put(PartyColumnHelper.DESCRIPTION_NAME, PartyColumnHelper.DESCRIPTION_NAME);
		partyProjectionMap.put(PartyColumnHelper.END, PartyColumnHelper.END);
	}

	private static class SimpleVideoDbHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "simple_parties.db";
		private static int DATABASE_VERSION = Math.abs(new Random().nextInt());
		// private static int DATABASE_VERSION = 2;
		private static String DROP_TABLE = "DROP TABLE IF EXISTS " + PARTY_TABLE_NAME + ";";

		SimpleVideoDbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {
			createTable(sqLiteDatabase);
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
			init(sqLiteDatabase);
		}

		@Override
		public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldv, int newv) {
			init(sqLiteDatabase);
		}

		private void init(SQLiteDatabase sqLiteDatabase) {
			dropTable(sqLiteDatabase);
			createTable(sqLiteDatabase);
		}

		private void dropTable(SQLiteDatabase sqLiteDatabase) {
			sqLiteDatabase.execSQL(DROP_TABLE);
		}

		private void createTable(SQLiteDatabase sqLiteDatabase) {
			String qs = "CREATE TABLE " + PARTY_TABLE_NAME + " (" + BaseColumns._ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + PartiesContract.PartyColumnHelper.TITLE + " TEXT, "
					+ PartiesContract.PartyColumnHelper.DESCRIPTION_NAME + " TEXT, "
					+ PartiesContract.PartyColumnHelper.START + " TEXT, " + PartiesContract.PartyColumnHelper.END
					+ " TEXT, " + PartiesContract.PartyColumnHelper.LONGITUDE + " REAL, "
					+ PartiesContract.PartyColumnHelper.LATITUDE + " REAL" + ");";

			sqLiteDatabase.execSQL(qs);
			// long pdb = populateDatabase(sqLiteDatabase);
			// Log.d("TestTag", Long.toString(pdb));
		}

		public long populateDatabase(SQLiteDatabase database) {
			String comment = "die, die, difefd223s!";
			ContentValues values = new ContentValues();
			values.put(PartiesContract.PartyColumnHelper.TITLE, comment);
			values.put(PartiesContract.PartyColumnHelper.DESCRIPTION_NAME, comment);
			values.put(PartiesContract.PartyColumnHelper.START, comment);
			// values.put(PartiesContract.PartyColumnHelper.URI_NAME, comment);
			long insertId = database.insert(PARTY_TABLE_NAME, null, values);
			return insertId;
		};
	}

	@Override
	public boolean onCreate() {
		mOpenDbHelper = new SimpleVideoDbHelper(getContext());
		RestApi.i().init(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case PARTIES:
			return PartiesContract.PartyColumnHelper.PARTY_DIR_CONTENT_TYPE;

		case PARTY_ID:
			return PartiesContract.PartyColumnHelper.PARTY_ITEM_CONTENT_TYPE;

		default:
			throw new IllegalArgumentException("Unknown video type: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String where, String[] whereArgs, String sortOrder) {
		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			// orderBy = PartiesContract.PartyColumnHelper.DEFAULT_SORT_ORDER;
			orderBy = null;
		} else {
			orderBy = sortOrder;
		}

		int match = URI_MATCHER.match(uri);

		Cursor c;
		switch (match) {
		case PARTIES:
			// query the database for all videos
			c = getDb().query(PARTY_TABLE_NAME, projection, where, whereArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(), PartiesContract.PartyColumnHelper.CONTENT_URI);
			break;
		case PARTY_ID:
			// query the database for a specific video
			long videoID = ContentUris.parseId(uri);
			c = getDb().query(PARTY_TABLE_NAME, projection,
					BaseColumns._ID + " = " + videoID + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
					whereArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(), PartiesContract.PartyColumnHelper.CONTENT_URI);
			break;
		default:
			throw new IllegalArgumentException("unsupported uri: " + uri);
		}

		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri

		int match = URI_MATCHER.match(uri);

		switch (match) {
		case PARTIES:
			ContentValues values;
			if (initialValues != null) {
				values = new ContentValues(initialValues);
			} else {
				values = new ContentValues();
			}

			// verifyValues(values);

			// insert the initialValues into a new database row
			SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
			long rowId = db.insert(PARTY_TABLE_NAME, PartiesContract.PartyColumnHelper.URI_PARTY_NAME, values);
			if (rowId > 0) {
				Uri videoURi = ContentUris.withAppendedId(PartiesContract.PartyColumnHelper.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(videoURi, null);
				return videoURi;
			}
			break;
		case PARTY_REST:
			RestApi.i().sendParty(Party.fromContent(initialValues));
			return null;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] allValues) {
		SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
		switch (URI_MATCHER.match(uri)) {
		case PARTIES:
			return insertParties(db, allValues);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	private int insertParties(SQLiteDatabase db, ContentValues[] allValues) {
		int rowsAdded = 0;
		long rowId;
		ContentValues values;
		try {
			db.beginTransaction();
			for (ContentValues initialValues : allValues) {
				values = initialValues == null ? new ContentValues() : new ContentValues(initialValues);
				rowId = insertParty(db, values);
				if (rowId > 0)
					rowsAdded++;
			}
			db.setTransactionSuccessful();
		} catch (SQLException ex) {
			Log.e(TAG, "There was a problem with the bulk insert: " + ex.toString());
		} finally {
			db.endTransaction();
		}
		getContext().getContentResolver().notifyChange(PartyColumnHelper.PARTIES_URI, null);
		return rowsAdded;
	}

	private long insertParty(SQLiteDatabase db, ContentValues values) {
		// if (!values.containsKey(TeamCaptainData.EventColumns.ORGANIZER))
		// throw new IllegalArgumentException("Missing event column '" +
		// TeamCaptainData.EventColumns.ORGANIZER + "'");
		// // ...do some processing (check for more missing fields, set default
		// values, etc.)
		return db.insert(PARTY_TABLE_NAME, null, values);
	}

	// private void verifyValues(ContentValues values) {
	// // Make sure that the fields are all set
	// if (!values.containsKey(PartiesContract.PartyColumnHelper.TITLE_NAME)) {
	// Resources r = Resources.getSystem();
	// values.put(PartiesContract.PartyColumnHelper.TITLE_NAME,
	// r.getString(android.R.string.untitled));
	// }
	//
	// if (!values
	// .containsKey(PartiesContract.PartyColumnHelper.DESCRIPTION_NAME)) {
	// values.put(PartiesContract.PartyColumnHelper.DESCRIPTION_NAME, "");
	// }
	//
	// if (!values.containsKey(PartiesContract.PartyColumnHelper.URI_NAME)) {
	// values.put(PartiesContract.PartyColumnHelper.URI_NAME, "");
	// }
	// }

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int match = URI_MATCHER.match(uri);
		int affected;

		switch (match) {
		case PARTIES:
			affected = getDb().delete(PARTY_TABLE_NAME, (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
					whereArgs);
			break;
		case PARTY_ID:
			long videoId = ContentUris.parseId(uri);
			affected = getDb().delete(PARTY_TABLE_NAME,
					BaseColumns._ID + "=" + videoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
					whereArgs);
			break;
		default:
			throw new IllegalArgumentException("unknown video element: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return affected;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int affected;

		switch (URI_MATCHER.match(uri)) {
		case PARTIES:
			affected = getDb().update(PARTY_TABLE_NAME, values, where, whereArgs);
			break;

		case PARTY_ID:
			String videoId = uri.getPathSegments().get(1);
			affected = getDb().update(PARTY_TABLE_NAME, values,
					BaseColumns._ID + "=" + videoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
					whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return affected;
	}

	private synchronized SQLiteDatabase getDb() {
		return mOpenDbHelper.getWritableDatabase();
	}
}
