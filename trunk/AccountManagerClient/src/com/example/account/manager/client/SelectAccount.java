package com.example.account.manager.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.os.Bundle;

public class SelectAccount extends ListActivity {

	Account[] accounts = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO layout
		
		AccountManager am = AccountManager.get(this);
		accounts = am.getAccountsByType("com.google");
		if (accounts == null || accounts.length <= 0) {
			// TODO add account
			finish();
		}
	}

}
