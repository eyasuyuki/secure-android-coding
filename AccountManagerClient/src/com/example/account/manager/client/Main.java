package com.example.account.manager.client;

import java.util.HashMap;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Main extends ListActivity {
    /** Called when the activity is first created. */
	Account account = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        checkAccount();
        initAccount();
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
        	AccountManager.get(this).getAccountsByType("com.google");
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
}
