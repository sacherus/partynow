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
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.sacherus.partynow.pojos.Party;
import com.sacherus.partynow.pojos.User;
import com.sacherus.partynow.provider.PartynowContracts.PartyColumnHelper;
import com.sacherus.partynow.provider.PartynowContracts.UserColumnHelper;
import com.sacherus.partynow.rest.RestApi;
import com.sacherus.utils.Utils;

/**
 * Simple content provider that demonstrates the basics of creating a content
 * provider that stores basic video meta-data.
 */
public class SimplePartyNowContentProvider extends ContentProvider {

	private static final String TAG = SimplePartyNowContentProvider.class.getName();
	public static final String PARTY_TABLE_NAME = "parties";
	public static final String USER_TABLE_NAME = "users";

	private static final int PARTIES = 1;
	private static final int PARTY_ID = 2;
	private static final int PARTY_REST = 3;

	private static final int USERS = 4;
	private static final int USER_ID = 5;
	private static final int USER_REST = 6;

	public static final UriMatcher PARTY_URI_MATCHER;
	static {
		PARTY_URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		PARTY_URI_MATCHER.addURI(PartynowContracts.SIMPLE_AUTHORITY, PartynowContracts.PartyColumnHelper.URI_PARTY_NAME,
				PARTIES);
		// use of the hash character indicates matching of an id
		PARTY_URI_MATCHER.addURI(PartynowContracts.SIMPLE_AUTHORITY, PartynowContracts.PartyColumnHelper.URI_PARTY_NAME
				+ "/#", PARTY_ID);
		PARTY_URI_MATCHER.addURI(PartynowContracts.SIMPLE_AUTHORITY,
				PartynowContracts.PartyColumnHelper.URI_PARTY_NAME_REST, PARTY_REST);
		PARTY_URI_MATCHER.addURI(PartynowContracts.SIMPLE_AUTHORITY, UserColumnHelper.URI_LAST_FRAGMENT, USERS);
		// use of the hash character indicates matching of an id
		PARTY_URI_MATCHER.addURI(PartynowContracts.SIMPLE_AUTHORITY, UserColumnHelper.URI_LAST_FRAGMENT + "/#", USER_ID);
		PARTY_URI_MATCHER.addURI(PartynowContracts.SIMPLE_AUTHORITY, UserColumnHelper.URI_LAST_FRAGMENT_REST, USER_REST);

	}

	private DbHelper mOpenDbHelper;

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
		partyProjectionMap.put(PartyColumnHelper.ORGANIZERS, PartyColumnHelper.ORGANIZERS);
		partyProjectionMap.put(PartyColumnHelper.PARTICIPANTS, PartyColumnHelper.PARTICIPANTS);
	}
	
	private void deleteAllRecords(SQLiteDatabase db, String tableName) {
		db.execSQL("delete from "+ tableName);
	}

	private static class DbHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "simple_parties.db";
		private static int DATABASE_VERSION = Math.abs(new Random().nextInt());
		// private static int DATABASE_VERSION = 2;
		

		DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {
			createPartyTable(sqLiteDatabase);
			createUserTable(sqLiteDatabase);
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
			dropTable(sqLiteDatabase, PARTY_TABLE_NAME);
			dropTable(sqLiteDatabase, USER_TABLE_NAME);
			createPartyTable(sqLiteDatabase);
			createUserTable(sqLiteDatabase);
		}

		private void dropTable(SQLiteDatabase sqLiteDatabase, String tableName) {
			String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName + ";";
			sqLiteDatabase.execSQL(DROP_TABLE);
		}

		// @formatter:off
		private void createPartyTable(SQLiteDatabase sqLiteDatabase) {
			String qs = "CREATE TABLE " + PARTY_TABLE_NAME + " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ PartynowContracts.PartyColumnHelper.TITLE + " TEXT, "
					+ PartynowContracts.PartyColumnHelper.DESCRIPTION_NAME + " TEXT, "
					+ PartyColumnHelper.ORGANIZERS + " TEXT, "
					+ PartyColumnHelper.PARTICIPANTS + " TEXT, "
					+ PartynowContracts.PartyColumnHelper.START + " TEXT, "
					+ PartynowContracts.PartyColumnHelper.END	+ " TEXT, "
					+ PartynowContracts.PartyColumnHelper.LONGITUDE + " REAL, "
					+ PartynowContracts.PartyColumnHelper.LATITUDE + " REAL" + ");";
			sqLiteDatabase.execSQL(qs);
			// long pdb = populateDatabase(sqLiteDatabase);
			// Log.d("TestTag", Long.toString(pdb));
		}
		
		
		private void createUserTable(SQLiteDatabase sqLiteDatabase) {
			String qs = "CREATE TABLE " + USER_TABLE_NAME + " (" 
					+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ UserColumnHelper.USERNAME + " TEXT,"
					+ UserColumnHelper.EMAIL + " TEXT"
					+ ")";
			sqLiteDatabase.execSQL(qs);
		}
		// @formatter:on
		public long populateDatabase(SQLiteDatabase database) {
			String comment = "die, die, difefd223s!";
			ContentValues values = new ContentValues();
			values.put(PartynowContracts.PartyColumnHelper.TITLE, comment);
			values.put(PartynowContracts.PartyColumnHelper.DESCRIPTION_NAME, comment);
			values.put(PartynowContracts.PartyColumnHelper.START, comment);
			// values.put(PartiesContract.PartyColumnHelper.URI_NAME, comment);
			long insertId = database.insert(PARTY_TABLE_NAME, null, values);
			return insertId;
		};
	}

	@Override
	public boolean onCreate() {
		mOpenDbHelper = new DbHelper(getContext());
		RestApi.i().init(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (PARTY_URI_MATCHER.match(uri)) {
		case PARTIES:
			return PartynowContracts.PartyColumnHelper.PARTY_DIR_CONTENT_TYPE;
		case PARTY_ID:
			return PartynowContracts.PartyColumnHelper.PARTY_ITEM_CONTENT_TYPE;

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

		int match = PARTY_URI_MATCHER.match(uri);

		Cursor c = null;
		switch (match) {
		case PARTIES:
			// query the database for all videos
			c = getDb().query(PARTY_TABLE_NAME, projection, where, whereArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(), PartynowContracts.PartyColumnHelper.CONTENT_URI);
			break;
		case PARTY_ID:
			// query the database for a specific video
			long videoID = ContentUris.parseId(uri);
			c = getDb().query(PARTY_TABLE_NAME, projection,
					BaseColumns._ID + " = " + videoID + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
					whereArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(), PartynowContracts.PartyColumnHelper.CONTENT_URI);
			break;
		case PARTY_REST:
			throw new IllegalArgumentException("Rest Need PARTY_REST need implementation");
		case USER_REST:
			RestApi.i().getUsers();
			break;
		case USERS:
			c = getDb().query(USER_TABLE_NAME, projection, where, whereArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(), UserColumnHelper.URI);
			break;
			
		default:
			throw new IllegalArgumentException("unsupported uri: " + uri);
		}

		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri

		int match = PARTY_URI_MATCHER.match(uri);

		switch (match) {
		case PARTIES:
			
			ContentValues values;
			if (initialValues != null) {
				values = new ContentValues(initialValues);
			} else {
				values = new ContentValues();
			}

			SQLiteDatabase db = mOpenDbHelper.getWritableDatabase();
			deleteAllRecords(db, PARTY_TABLE_NAME);
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(PARTY_TABLE_NAME);
			qb.setProjectionMap(partyProjectionMap);
			// @formatter:off
			Cursor c = (Cursor) qb.query(db, //database to put data in, columns to
					new String[] { PartynowContracts.PartyColumnHelper._ID }, // The columns to return from the query
					null, // The columns for the where clause
					null, // The values for the where clause
					null, // don't group the rows
					null, // don't filter by row groups
					null // The sort order
					);
			// @formatter:on

			long rowId = db.insert(PARTY_TABLE_NAME, PartynowContracts.PartyColumnHelper.URI_PARTY_NAME, values);
			Utils.log(rowId);
			if (rowId > 0) {
				Uri videoURi = ContentUris.withAppendedId(PartynowContracts.PartyColumnHelper.CONTENT_URI, rowId);
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
		int retVal = 0;
		switch (PARTY_URI_MATCHER.match(uri)) {
		case PARTIES:
			retVal = insertBulk(db, allValues, PartyColumnHelper.PARTIES_URI, PARTY_TABLE_NAME);
			break;
		case USERS:
			retVal = insertBulk(db, allValues, UserColumnHelper.URI, USER_TABLE_NAME);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return retVal;
	}

	private int insertBulk(SQLiteDatabase db, ContentValues[] allValues, Uri uri, String table) {
		int rowsAdded = 0;
		long rowId;
		ContentValues values;
		try {
			db.beginTransaction();
			deleteAllRecords(db, table);
			for (ContentValues initialValues : allValues) {
				values = initialValues == null ? new ContentValues() : new ContentValues(initialValues);
				rowId = insertElementInBulk(db, values, table);
				if (rowId > 0)
					rowsAdded++;
			}
			db.setTransactionSuccessful();
		} catch (SQLException ex) {
			Log.e(TAG, "There was a problem with the bulk insert: " + ex.toString());
		} finally {
			db.endTransaction();
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAdded;
	}

	private long insertElementInBulk(SQLiteDatabase db, ContentValues values, String table) {
		return db.insert(table, null, values);
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
		int match = PARTY_URI_MATCHER.match(uri);
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

		switch (PARTY_URI_MATCHER.match(uri)) {
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
