package com.example.account.manager.client;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.example.account.manager.client.provider.FoodLog;

/*
 * 下記を参考にしました
 * Authenticating against App Engine from an Android app
 * http://blog.notdot.net/2010/05/Authenticating-against-App-Engine-from-an-Android-app
 */
public class AsyncUpdate extends AsyncTask<Void, Void, Void> {
	private static final String TAG = AsyncUpdate.class.getName();

	Context context = null;
	DefaultHttpClient client = null;
	String token = null;
	ProgressDialog dialog = null;
	String content = null;
	JSONArray array = null;
	List<FoodLog> logList = new ArrayList<FoodLog>();
	ListView view = null;
	
	public AsyncUpdate(Context context,
			DefaultHttpClient client,
			String token,
			ListView view,
			ProgressDialog dialog) {
		this.context = context;
		this.client = client;
		this.token = token;
		this.view = view;
		this.dialog = dialog;
	}

	@Override
	protected Void doInBackground(Void... params) {
		URI uri = URI.create(context.getString(R.string.json_uri));
		HttpResponse response = null;
		InputStream in = null;
		try {
			for (int i=0; i<AsyncCookie.RETRY_MAX; i++) {
				HttpGet get = new HttpGet(uri);
				response = client.execute(get);
				int code = HttpStatus.SC_UNAUTHORIZED;
				StatusLine status = response.getStatusLine();
				if (status != null) {
					code = status.getStatusCode();
					if (code == HttpStatus.SC_OK || code == HttpStatus.SC_CREATED) {
						break;
					}
				}
			}
			HttpEntity entity = response.getEntity();
			in = entity.getContent();
			StringBuffer buf = new StringBuffer();
			int c = -1;
			while ((c = in.read()) > -1) {
				buf.append((char)c);
			}
			content = buf.toString();
			Log.d(TAG, "doInBackground: content="+content);
			if (content != null && content.length() > 0) {
				content2FoodLogs(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	void content2FoodLogs(String content) {
		try {
			array = new JSONArray(content);
			Log.d(TAG, "content2FoodLog: array="+array.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (array != null) {
			for (int i=0; i<array.length(); i++) {
				JSONObject jobj = null;
				try {
					jobj = array.getJSONObject(i);
					Log.d(TAG, "content2FoodLog: array["+i+"]="+jobj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (jobj != null) {
					FoodLog foodLog = new FoodLog(jobj);
					logList.add(foodLog);
				}
			}
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		FoodLogAdapter adapter =
			new FoodLogAdapter(
					context,
					R.layout.food_log_row,
					logList);
		view.setAdapter(adapter);
        try { dialog.dismiss(); } catch (Exception e) {}
	}
}
