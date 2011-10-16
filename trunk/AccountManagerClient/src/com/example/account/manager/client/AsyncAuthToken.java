package com.example.account.manager.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

public class AsyncAuthToken extends AsyncTask<Void, Void, Void> {
    private static final String TAG =
        AsyncAuthToken.class.getName();

    public static final String APP_ENGINE = "ah";

    Context context = null;
    Handler handler = null;
    Account account = null;
    AccountManagerCallback<Bundle> callback = null;
    AsyncTask<String, Void, Void> next = null;
    ProgressDialog dialog = null;

    String authToken = null;

    public AsyncAuthToken(
            Context context,
            Handler handler,
            Account account,
            AccountManagerCallback<Bundle> callback,
            AsyncTask<String, Void, Void> next) {
        this.context = context;
        this.handler = handler;
        this.callback = callback;
        this.next = next;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.auth_token_title);
        try { dialog.show(); } catch (Exception e) {}
    }

    Account getAccountFromPrefs() {
        Account result = null;
        SharedPreferences sp =
            PreferenceManager.getDefaultSharedPreferences(context);
        if (sp.contains(SelectAccount.DEFAULT_ACCOUNT_KEY)) {
            String def =
                sp.getString(
                        SelectAccount.DEFAULT_ACCOUNT_KEY, null);
            if (def != null) {
                AccountManager am = AccountManager.get(context);
                Account[] accounts =
                    am.getAccountsByType(Main.ACCOUNT_TYPE);
                for (Account a : accounts) {
                    if (a.name != null && def.equals(a.name)) {
                        result = a;
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    protected Void doInBackground(Void... params) {
        account = getAccountFromPrefs();
        if (account != null) {
            AccountManager am = AccountManager.get(context);
            AccountManagerFuture<Bundle> future =
                am.getAuthToken(
                        account,
                        APP_ENGINE,
                        true,
                        callback, handler);
            try {
                authToken =
                    future.getResult().getString(
                            AccountManager.KEY_AUTHTOKEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "doInBackground: authToken=" + authToken);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (authToken != null && next != null) {
            next.execute(authToken);
        }
        try { dialog.dismiss(); } catch (Exception e) {}
    }
}