package com.example.account.manager.client;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/*
 * 下記を参考にしました
 * Authenticating against App Engine from an Android app
 * http://blog.notdot.net/2010/05/Authenticating-against-App-Engine-from-an-Android-app
 */
public class AsyncCookie extends AsyncTask<String, Void, Void> {
    private static final String TAG =
        AsyncCookie.class.getName();

    public static final String SACSID = "SACSID";
    public static final int RETRY_MAX = 2;

    Context context = null;
    DefaultHttpClient client = null;
    TokenListener listener = null;
    AsyncTask<Void, Void, Void> next = null;
    ProgressDialog dialog = null;
    boolean isValidCookie = false;

    public AsyncCookie(
            Context context,
            DefaultHttpClient client,
            TokenListener listener,
            AsyncTask<Void, Void, Void> next) {
        this.context = context;
        this.client = client;
        this.listener = listener;
        this.next = next;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.cookie_title);
        try { dialog.show(); } catch (Exception e) {}
    }

    @Override
    protected Void doInBackground(String... params) {
        URI uri =
            URI.create(
                    context.getString(R.string.login_uri)
                    + params[0]);
        HttpGet get = new HttpGet(uri);
        client.getParams().setBooleanParameter(
                ClientPNames.HANDLE_REDIRECTS,
                false);
        for (int i = 0; i < RETRY_MAX; i++) {
            try {
                HttpResponse response = client.execute(get);
                List<Cookie> clist =
                    client.getCookieStore().getCookies();
                for (Cookie cookie : clist) {
                    Log.d(TAG, "doInBackgroud: cookie.getName()=" + cookie.getName());
                    if (cookie.getName().equals(SACSID)) {
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
        }
        try { dialog.dismiss(); } catch (Exception e) {}
    }
}
