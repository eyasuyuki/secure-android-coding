package com.example.account.manager.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Main extends ListActivity {
	private static final String TAG = Main.class.getName();
	
	public static final int GET_ACCOUNT = 1;
	public static final String ACCOUNT_TYPE = "com.google";
	public static final String APP_ENGINE = "ah";
    public static final String AUTH_TOKEN_KEY = "authtoken";

	Handler handler = new Handler();

	Account account = null;
	String authToken = null;
	
	
	final class NutrimancerCallback implements AccountManagerCallback<Bundle> {
		@Override
		public void run(AccountManagerFuture<Bundle> future) {
			try {
				Bundle result = future.getResult();
				if (result.containsKey(AccountManager.KEY_INTENT)) {
				        Intent intent =
				                (Intent) result.get(AccountManager.KEY_INTENT);
				        Main.this.startActivityForResult(intent, Main.GET_ACCOUNT);
				        return;
				}
				
				authToken =
				        result.getString(AccountManager.KEY_AUTHTOKEN);
				Log.d(TAG, "getAuthToken: authToken="+authToken);
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        checkAccount();
        initAccount();
        
        // login by account manager
        AccountManager am = AccountManager.get(this);
        AccountManagerFuture<Bundle> future =
        	am.getAuthToken(account, APP_ENGINE, true, new NutrimancerCallback(), handler);
        try {
			authToken = future.getResult().getString(AUTH_TOKEN_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
        Log.d(TAG, "onCreate: authToken="+authToken);
    }
    
    void checkAccount() {
    	SharedPreferences sp =
    		PreferenceManager.getDefaultSharedPreferences(this);
    	String key = getString(R.string.default_account_key);
    	if (!sp.contains(key)) {
    		startSelectAccount();
    	}
    }
    
    void startSelectAccount() {
		Intent intent = new Intent();
		intent.setClass(this, SelectAccount.class);
		intent.setFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		startActivity(intent);
    }
    
    void initAccount() {
        Account[] accounts =
        	AccountManager.get(this).getAccountsByType(ACCOUNT_TYPE);
        Map<String, Account> ahash = new HashMap<String, Account>();
        for (Account a: accounts) {
        	ahash.put(a.name, a);
        }
    	
        SharedPreferences sp =
    		PreferenceManager.getDefaultSharedPreferences(this);
    	
        String key = getString(R.string.default_account_key);
    	String ac = sp.getString(key, null);
    	if (ac != null) {
    		account = ahash.get(ac);
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_select_account:
			startSelectAccount();
			break;
		}
		return true;
	}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            //Log.d(TAG, "requestCode="+requestCode+", resultCode="+resultCode+", data"+data);
            switch (requestCode) {
            case GET_ACCOUNT:
                    if (resultCode == RESULT_OK && data != null) {
                            authToken = data.getStringExtra(AUTH_TOKEN_KEY);
                            Log.d(TAG, "onActivityResult: authToken="+authToken);
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
    }
}
