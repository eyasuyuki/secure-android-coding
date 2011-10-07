package com.example.account.manager.client.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class FoodLogProvider extends ContentProvider {
	public static final String AUTHORITY          = FoodLogProvider.class.getName();
	public static final Uri    CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + "foodLog");
	public static final String DATABASE_NAME      = "food_log";
	public static final int    DATABASE_VERSION   = 2;
	public static final String TABLE_NAME         = "food_logs";
	public static final String COLUMN_KEY         = "key";
	public static final String COLUMN_VERSION     = "version";
	public static final String COLUMN_USER        = "user";
	public static final String COLUMN_LOG_DATE    = "log_date";
	public static final String COLUMN_TIME        = "log_time";
	public static final String COLUMN_FOOD        = "food";
	public static final String COLUMN_KCAL        = "kcal";
	public static final String COLUMN_SALT        = "salt";
	public static final String COLUMN_CREATE_DATE = "create_date";
	public static final String[] COLUMNS = {
		BaseColumns._ID
		,COLUMN_KEY
		,COLUMN_VERSION
		,COLUMN_USER
		,COLUMN_LOG_DATE
		,COLUMN_TIME
		,COLUMN_FOOD
		,COLUMN_KCAL
		,COLUMN_SALT
		,COLUMN_CREATE_DATE
	};
	private static final int LOGS = 1;
	private static final int LOG = 2;
	private static final long UNKNOWN_ID = -1L;
	
	private static final String CONTENT_TYPE
		= "vnd.android.cursor.dir/example.account.manager.client";

	private static final String CONTENT_ITEM_TYPE
		= "vnd.android.cursor.item/example.account.manager.client";

	private UriMatcher matcher;
	private Helper helper;

	@Override
	public boolean onCreate() {
		matcher = new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(AUTHORITY, "foodLog", LOGS);
		matcher.addURI(AUTHORITY, "foodLog/#", LOG);
		helper = new Helper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case LOGS:
			return CONTENT_TYPE;
		case LOG:
			return CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	public static long parseId(Uri uri) {
		long result = UNKNOWN_ID;
		String path = uri.getPathSegments().get(1);
		if (path != null || path.length() > 0) {
			try {
				result = Long.parseLong(path);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	String createSelection(Uri uri, String selection) {
		long id = parseId(uri);
		if (id == UNKNOWN_ID) {
			return selection;
		} else {
			return BaseColumns._ID + " = ? " + (selection == null ? "" : " AND " + selection);
		}
	}
	
	String[] createArgs(Uri uri, String[] selectionArgs) {
		long id = parseId(uri);
		if (id == UNKNOWN_ID) {
			return selectionArgs; 
		} else if (selectionArgs != null) {
			String[] args = new String[selectionArgs.length + 1];
			System.arraycopy(selectionArgs, 0, args, 0, selectionArgs.length);
			args[selectionArgs.length] = Long.toString(id);
			return args;
		} else {
			return new String[] { Long.toString(id) };
		}
	}

	@Override
  	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = helper.getWritableDatabase();
		final int deleteCount;
		switch (matcher.match(uri)) {
		case LOGS:
			deleteCount = db.delete(TABLE_NAME, selection, selectionArgs);
			break;
		case LOG:
			final String idPlusSelection = createSelection(uri, selection);
			final String[] args = createArgs(uri, selectionArgs);
			deleteCount = db.delete(TABLE_NAME, idPlusSelection, args);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return deleteCount;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = helper.getWritableDatabase();
		if(matcher.match(uri) != LOGS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		final long id = db.insertOrThrow(TABLE_NAME, null, values);

		// 変更を通知する
	    final Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
	    getContext().getContentResolver().notifyChange(newUri, null);

		return newUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		final String idPlusSelection = createSelection(uri, selection);
		final String[] args = createArgs(uri, selectionArgs);
		final SQLiteDatabase db = helper.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME, projection, idPlusSelection, args, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = helper.getWritableDatabase();
		final int updateCount;
		switch (matcher.match(uri)) {
		case LOGS:
			updateCount = db.update(TABLE_NAME, values, selection, selectionArgs);
			break;
		case LOG:
			final String idPlusSelection = createSelection(uri, selection);
			String[] args = createArgs(uri, selectionArgs);
			updateCount = db.update("random", values, idPlusSelection, args);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return updateCount;
	}
	
    final class Helper extends SQLiteOpenHelper {
        Helper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        private static final String CREATE_FOOD_LOG_TABLE_SQL =
        	"CREATE TABLE "              + TABLE_NAME
        	+ " ( " + BaseColumns._ID    + " INTEGER PRIMARY KEY "
        	+ " , " + COLUMN_KEY         + " TEXT "
        	+ " , " + COLUMN_VERSION     + " INTEGER "
            + " , " + COLUMN_USER        + " TEXT "
            + " , " + COLUMN_LOG_DATE    + " TEXT "
            + " , " + COLUMN_TIME        + " TEXT "
            + " , " + COLUMN_FOOD        + " TEXT "
            + " , " + COLUMN_KCAL        + " NUMBER "
            + " , " + COLUMN_SALT        + " NUMBER "
            + " , " + COLUMN_CREATE_DATE + " TEXT "
            + " ) ";
        
        @Override
        public void onCreate(SQLiteDatabase db) {
          db.execSQL(CREATE_FOOD_LOG_TABLE_SQL);			
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        	onCreate(db);			
        }
    }
}
