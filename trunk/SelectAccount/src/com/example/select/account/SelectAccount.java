package com.example.select.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectAccount extends ListActivity {
	public static final String AUTHORITIES_FILTER_KEY = "authorities";
	Account[] accounts = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		AccountManager am = AccountManager.get(this);

		accounts = am.getAccountsByType("com.google");
		if (accounts == null || accounts.length <= 0) {
			// add account
			Intent intent =
				new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
			intent.putExtra(
					AUTHORITIES_FILTER_KEY,
					new String[]{ ContactsContract.AUTHORITY });
			startActivity(intent);
			// exit
			finish();
		}

        // single account
        if (accounts.length == 1) {
        	saveToPreference(accounts[0].name);
        	// finish();
        }
		
		ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
		String[] names = new String[accounts.length];
		for (int i=0; i<accounts.length; i++) {
			names[i] = accounts[i].name;
		}
		
		ArrayAdapter<String> adapter =
			new ArrayAdapter<String>(
					this,
					android.R.layout.simple_list_item_single_choice,
					names);
        setListAdapter(adapter);
    }
    
    void saveToPreference(String name) {
		SharedPreferences sp =
			PreferenceManager.getDefaultSharedPreferences(this);
		String key = getString(R.string.default_account_key);
		Editor editor = sp.edit();
		editor.putString(key, name);
		editor.commit();
    }

    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    	if (accounts != null
    		&& accounts.length > 0
    		&& accounts.length >= position) {
    		Account account = accounts[position];
    		saveToPreference(account.name);
    		finish();
    	}
	}
}
