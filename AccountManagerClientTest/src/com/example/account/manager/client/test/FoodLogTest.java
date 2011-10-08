package com.example.account.manager.client.test;

import java.util.Date;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.account.manager.client.provider.FoodLog;

public class FoodLogTest extends TestCase {
	private static final String JSON = "{\"time\":\"Breakfast\",\"food\":\"Sausage\",\"kcal\":200,\"logDate\":1317686400000,\"createDate\":1317685350549,\"user\":\"106284720413574545216\",\"key\":\"agtudXRyaW1hbmNlcnINCxIHRm9vZExvZxgCDA\",\"version\":1,\"salt\":2}";
	private static final String KEY = "agtudXRyaW1hbmNlcnINCxIHRm9vZExvZxgCDA";
	private static final long VERSION = 1L;
	private static final String USER = "106284720413574545216";
	private static final long LDATE = 1317686400000L;
	private static final String TIME = "Breakfast";
	private static final String FOOD = "Sausage";
	private static final double KCAL = 200.0;
	private static final double SALT = 2.0;
	private static final long CTIME = 1317685350549L;

	public void testCreate() throws JSONException {
		FoodLog foodLog = new FoodLog(new JSONObject(JSON));
		assertNotNull(foodLog);
		assertEquals(KEY, foodLog.getKey());
		assertEquals(VERSION, foodLog.getVersion());
		assertEquals(USER, foodLog.getUser());
		assertEquals(FoodLog.DF.format(new Date(LDATE)), foodLog.getLogDate());
		assertEquals(TIME, foodLog.getTime());
		assertEquals(FOOD, foodLog.getFood());
		assertEquals(KCAL, foodLog.getKcal());
		assertEquals(SALT, foodLog.getSalt());
		assertEquals(FoodLog.DTF.format(new Date(CTIME)), foodLog.getCreateDate());
	}
}
