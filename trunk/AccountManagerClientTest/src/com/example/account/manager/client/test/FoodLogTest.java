package com.example.account.manager.client.test;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.account.manager.client.provider.FoodLog;

public class FoodLogTest extends TestCase {
	private static final String JSON = "{\"time\":\"Breakfast\",\"food\":\"Sausage\",\"kcal\":0,\"logDate\":1317686400000,\"createDate\":1317685350549,\"user\":\"106284720413574545216\",\"key\":\"agtudXRyaW1hbmNlcnINCxIHRm9vZExvZxgCDA\",\"version\":1,\"salt\":2}";

	public void testCreate() throws JSONException {
		FoodLog foodLog = new FoodLog(new JSONObject(JSON));
		assertNotNull(foodLog);
	}
}
