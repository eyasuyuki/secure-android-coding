package com.example.account.manager.client.test;

import java.text.SimpleDateFormat;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;

import com.example.account.manager.client.provider.FoodLog;
import com.example.account.manager.client.provider.FoodLogProvider;

public class FoodLogProviderTest extends AndroidTestCase {
	SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

	Uri insert(FoodLog foodLog) {
		Uri result = null;
		ContentValues values = new ContentValues();
 		values.put(FoodLogProvider.COLUMN_KEY,      foodLog.getKey());
		values.put(FoodLogProvider.COLUMN_LOG_DATE, foodLog.getLogDate());
		values.put(FoodLogProvider.COLUMN_TIME,     foodLog.getTime());
		values.put(FoodLogProvider.COLUMN_FOOD,     foodLog.getFood());
		values.put(FoodLogProvider.COLUMN_KCAL,     foodLog.getKcal());
		values.put(FoodLogProvider.COLUMN_SALT,     foodLog.getSalt());
		Context context = getContext();
		result = context.getContentResolver().insert(FoodLogProvider.CONTENT_URI, values);
		return result;
	}
	
	int delete(FoodLog foodLog) {
		int result = 0;
		Context context = getContext();
		Uri uri = null;
		if (foodLog != null) {
			uri = ContentUris.withAppendedId(FoodLogProvider.CONTENT_URI, foodLog.getId());
		} else {
			uri = FoodLogProvider.CONTENT_URI;
		}
		result = context.getContentResolver().delete(uri, null, null);
		return result;
	}
	
	FoodLog query(long id) {
		FoodLog result = null;
		Context context = getContext();
		Uri uri = ContentUris.withAppendedId(FoodLogProvider.CONTENT_URI, id);
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, FoodLogProvider.COLUMNS, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					result = new FoodLog();
					int i = 0;
					result.setId(cursor.getInt(i++));
					result.setKey(cursor.getString(i++));
					result.setVersion(cursor.getLong(i++));
					result.setUser(cursor.getString(i++));
					result.setLogDate(cursor.getString(i++));
					result.setTime(cursor.getString(i++));
					result.setFood(cursor.getString(i++));
					result.setKcal(cursor.getDouble(i++));
					result.setSalt(cursor.getDouble(i++));
					result.setCreateDate(cursor.getString(i++));
					//if (!cursor.isClosed()) cursor.close();
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//if (cursor != null) cursor.close();
		}
		return result;
	}
	
	FoodLog createData() {
		FoodLog foodLog = new FoodLog();
		foodLog.setKey("aaaaaaabbbbbbbbccccccccc");
		foodLog.setLogDate("2011-10-12");
		foodLog.setTime("Dinner");
		foodLog.setFood("Takoyaki");
		foodLog.setKcal(600.0);
		foodLog.setSalt(4.0);
		return foodLog;
	}
	
	public void test() {
		FoodLog foodLog = createData();
		Uri uri = insert(foodLog);
		assertNotNull(uri);
		long id = FoodLogProvider.parseId(uri);
		FoodLog result = query(id);
		assertNotNull(result);
		assertEquals(foodLog.getKcal(), result.getKcal());
		assertEquals(foodLog.getLogDate(), result.getLogDate());
		assertEquals(foodLog.getTime(), result.getTime());
		assertEquals(foodLog.getFood(), result.getFood());
		assertEquals(foodLog.getKcal(), result.getKcal());
		assertEquals(foodLog.getSalt(), result.getSalt());
		FoodLog log2 = createData();
		Uri uri2 = insert(log2);
		assertNotNull(uri2);
		id = FoodLogProvider.parseId(uri2);
		FoodLog result2 = query(id);
		assertNotNull(result2);
		assertNotSame(result, result2);
		int count = delete(result);
		assertEquals(1, count);
		count = delete(result2);
		assertEquals(1, count);
		insert(createData());
		insert(createData());
		insert(createData());
		count = delete(null);
		assertNotSame(0, count);
	}
}
