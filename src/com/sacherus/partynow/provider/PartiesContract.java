package com.sacherus.partynow.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Public API for the example FinchVideo caching content provider example.
 * 
 * The public API for a content provider should only contain information that
 * should be referenced by content provider clients. Implementation details such
 * as constants only used by a content provider subclass should not appear in
 * the provider API.
 */
public class PartiesContract {
	// public static final Map<String, String> PARTY_CONVERSION;
	// static {
	// Map<String, String> aMap = new HashMap<String, String>();
	// aMap.put("title", PartyColumnHelper.TITLE);
	// aMap.put("id", PartyColumnHelper._ID);
	// // amap.put("latitude", )
	// PARTY_CONVERSION = Collections.unmodifiableMap(aMap);
	// }

	public static final int ID_COLUMN = 0;
	public static final int TITLE_COLUMN = 1;
	public static final int DESCRIPTION_COLUMN = 2;

	// public static final String AUTHORITY = "com.sacherus.partynow";

	public static final String SIMPLE_AUTHORITY = "com.sacherus.partynow";

	/**
	 * Simple Videos columns
	 */
	public static final class PartyColumnHelper implements BaseColumns {
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		// This class cannot be instantiated
		private PartyColumnHelper() {
		}

		// uri references all videos
		private static final String PARTIES_URI_STRING = "content://" + SIMPLE_AUTHORITY + "/"
				+ PartyColumnHelper.URI_PARTY_NAME;
		public static final Uri PARTIES_URI = Uri.parse(PartyColumnHelper.PARTIES_URI_STRING);
		public static final Uri PARTIES_URI_REST = Uri.withAppendedPath(PartyColumnHelper.PARTIES_URI,
				PartyColumnHelper.URI_REST_FLAG);

		/**
		 * The content:// style URI for this table
		 */
		public static final Uri CONTENT_URI = PARTIES_URI;

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String PARTY_DIR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.partynow.party";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * video.
		 */
		public static final String PARTY_ITEM_CONTENT_TYPE = "vnd.android.cursor.item/vnd.partynow.party";

		/**
		 * The video itself
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String URI_PARTY_NAME = "party";
		public static final String URI_REST_FLAG = "r";
		public static final String URI_PARTY_NAME_REST = URI_PARTY_NAME + "/" + URI_REST_FLAG;

		/**
		 * Column name for the title of the video
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String TITLE = "title";

		/**
		 * Column name for the description of the video.
		 */
		public static final String DESCRIPTION_NAME = "description";

		/**
		 * Column name for the media uri
		 */
		// public static final String URI_NAME = "uri";

		public static final String LONGITUDE = "longitude";

		public static final String LATITUDE = "latitude";

		public static final String START = "start";

		public static final String END = "end";
	}

}
