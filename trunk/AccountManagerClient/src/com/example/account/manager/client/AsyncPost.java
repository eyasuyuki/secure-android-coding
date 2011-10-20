package com.example.account.manager.client;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.example.account.manager.client.provider.FoodLog;

public class AsyncPost extends AsyncTask<Void, Void, Void> {
	private static final String TAG = AsyncPost.class.getName();

	ProgressDialog dialog = null;
	Activity activity = null;
	DefaultHttpClient client = null;
	String logDate = null;
	String time = null;
	String food = null;
	String kcal = null;
	String salt = null;
	boolean success = false;

	public AsyncPost(
			Activity activity,
			DefaultHttpClient client,
			String logDate,
			String time,
			String food,
			String kcal,
			String salt) {
		this.activity = activity;
		this.client = client;
		this.logDate = logDate;
		this.time = time;
		this.food = food;
		this.kcal = kcal;
		this.salt = salt;
	}
	
	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(activity);
        dialog.setTitle(R.string.post_title);
        try { dialog.show(); } catch (Exception e) {}
	}
	static String encode(String value) {  
        try {  
            return URLEncoder.encode(value, HTTP.UTF_8);  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
            throw new RuntimeException(e);  
        }  
    }  	
	@Override
	protected Void doInBackground(Void... params) {
	    URI uri = URI.create(activity.getString(R.string.post_uri));
		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
		HttpResponse response = null;
		InputStream in = null;
		try {
			for (int i=0; i<AsyncCookie.RETRY_MAX; i++) {
				HttpPost post = new HttpPost(uri);
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(5);
		        pairs.add(new BasicNameValuePair(FoodLog.LOG_DATE_KEY, logDate));
		        pairs.add(new BasicNameValuePair(FoodLog.TIME_KEY, time));
		        pairs.add(new BasicNameValuePair(FoodLog.FOOD_KEY, food));
		        pairs.add(new BasicNameValuePair(FoodLog.KCAL_KEY, kcal));
		        pairs.add(new BasicNameValuePair(FoodLog.SALT_KEY, salt));
		        post.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		        response = client.execute(host, post);
				int code = HttpStatus.SC_UNAUTHORIZED;
				StatusLine status = response.getStatusLine();
				if (status != null) {
					code = status.getStatusCode();
					Log.d(TAG, "doInBackground: code="+code);
					if (code == HttpStatus.SC_OK
						|| code == HttpStatus.SC_CREATED
						|| code == HttpStatus.SC_MOVED_TEMPORARILY) {
                        success = true;
						break;
					}
				}
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

	@Override
	protected void onPostExecute(Void result) {
        try { dialog.dismiss(); } catch (Exception e) {}
        if (success) {
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
	}
}
