package com.example.account.manager.client;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

/*
 * 下記を参考にしました
 * Authenticating against App Engine from an Android app
 * http://blog.notdot.net/2010/05/Authenticating-against-App-Engine-from-an-Android-app
 */
public class AsyncCookie extends AsyncTask<Void, Void, Void> {
	private static final String TAG = AsyncCookie.class.getName();
	public static final int RETRY_MAX = 2;

	Context context = null;
	//Handler handler = null;
	DefaultHttpClient client = null;
	String token = null;
	ListView view = null;
	TokenListener listener = null;
	AsyncTask<Void, Void, Void> next = null;
	ProgressDialog dialog = null;
	boolean isValidCookie = false;
	
	public AsyncCookie(Context context,
			//Handler handler,
			ProgressDialog dialog,
			DefaultHttpClient client,
			String token,
			ListView view,
			TokenListener listener,
			AsyncTask<Void, Void, Void> next) {
		this.context = context;
		//this.handler = handler;
		this.dialog = dialog;
		this.client = client;
		this.token = token;
		this.view = view;
		this.listener = listener;
		this.next = next;
	}
	
	@Override
	protected void onPreExecute() {
        dialog.setTitle(R.string.progress_title);
        try {
            dialog.show();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Override
	protected Void doInBackground(Void... params) {
    	
		URI uri = URI.create(context.getString(R.string.login_uri)+token);
    	HttpGet get = new HttpGet(uri); 
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		for (int i=0; i<RETRY_MAX; i++) {
	        try {
	        	HttpResponse response = client.execute(get);
				for(Cookie cookie : client.getCookieStore().getCookies()) {
					Log.d(TAG, ""+cookie.getName());
                    if(cookie.getName().equals("SACSID")) {
                    	isValidCookie = true;
                    	return null;
                    }
				}
				if (!isValidCookie) {
					if (listener != null) {
						synchronized (listener) {
							listener.invalidate();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (isValidCookie && next != null) {
			next.execute();
		} else {
	        try { dialog.dismiss(); } catch (Exception e) {}
		}
	}
}
