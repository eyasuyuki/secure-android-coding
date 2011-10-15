package com.example.basic.client;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

public class AsyncLogin extends AsyncTask<Void, Void, Void> {

	Context context = null;
	String uriString = null;
	String user = null;
	String password = null;
	TextView view = null;
	
	int code = HttpStatus.SC_UNAUTHORIZED;
	String content = null;
	String failed = null;
	ProgressDialog dialog = null;
	
	public AsyncLogin(Context context, String uriString, String user, String password, TextView view) {
		this.context = context;
		this.uriString = uriString;
		this.user = user;
		this.password = password;
		this.view = view;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.progress_title);
	    try { dialog.show();} catch (Exception e) {}
	}

    /*
     * 下記を参考にしました:
     * HttpClient Tutorial - Chapter 4. HTTP authentication
     * http://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html
     */
	@Override
	protected Void doInBackground(Void... params) {
    	final int RETRY_MAX = 2;
    	
		URI uri = URI.create(uriString);
    	HttpHost host =
    		new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()); 
		DefaultHttpClient client =
			new DefaultHttpClient();

		// set username-password credentials
		if (user != null && user.length() > 0) {
			client.getCredentialsProvider().setCredentials(
					new AuthScope(uri.getHost(), uri.getPort()),
					new UsernamePasswordCredentials(user, password));
		}

		HttpContext context = new BasicHttpContext();
		HttpGet get = new HttpGet(uriString);

		HttpResponse response = null;

		try {
			for (int i=0; i<RETRY_MAX; i++) {
				response = client.execute(host, get, context);
				code = response.getStatusLine().getStatusCode();
				if (code == HttpStatus.SC_OK
					|| code == HttpStatus.SC_CREATED) {
					HttpEntity entity = response.getEntity();
					StringBuffer buf = new StringBuffer();
					InputStream in = null;
					try {
						in = entity.getContent();
						int c = -1;
						while ((c = in.read()) > -1) {
							buf.append((char)c);
						}
						content = buf.toString();
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
			}
			// fail
			failed = "Response code is " + Integer.toString(code);
		} catch (Exception e) {
			e.printStackTrace();
			if (get != null) {
				get.abort();
			}
			failed =
				"Response code is " + Integer.toString(code)
				+ ", mesage:" + e.getLocalizedMessage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (content != null) {
			view.setText(content);
		} else {
			view.setText(failed);
		}
	    try { dialog.dismiss(); } catch (Exception e) {}
	}

}
