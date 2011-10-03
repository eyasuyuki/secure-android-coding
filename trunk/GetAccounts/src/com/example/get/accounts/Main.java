package com.example.get.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
 
        AccountManager am = AccountManager.get(this);
        am.getAccounts();
        StringBuffer buf = new StringBuffer();
        buf.append("Accounts:\n");
        for (Account a: am.getAccounts()) {
        	buf.append(a.name);
        	buf.append("\n");
        }
        
        TextView messageText = (TextView)findViewById(R.id.message_tex);
       messageText.setText(buf.toString());
    }
}