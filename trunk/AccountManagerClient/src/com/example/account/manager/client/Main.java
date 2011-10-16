package com.example.account.manager.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Main extends ListActivity {
	private static final String TAG = Main.class.getName();
	
	public static final int ACCOUNT_SELECTED = 1;
	public static final int GRANT_AUTH_TOKEN = 2;
	public static final String ACCOUNT_TYPE = "com.google";
	Handler handler = new Handler();
	DefaultHttpClient client = null;;

	Account account = null;
	String authToken = null;
	
	final class InvalidateTokenListener implements TokenListener {
		@Override
		public void invalidate() {
			AccountManager am = AccountManager.get(Main.this);
			am.invalidateAuthToken(AsyncAuthToken.APP_ENGINE, authToken); // dispose token
			authToken = null;
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							Main.this,
							Main.this.getString(R.string.invalidate_token_message),
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	final class NutrimancerCallback implements AccountManagerCallback<Bundle> {
		private final String TAG = NutrimancerCallback.class.getName();
		@Override
		public void run(AccountManagerFuture<Bundle> future) {
			try {
				Bundle result = future.getResult();
				if (result.containsKey(AccountManager.KEY_INTENT)) {
				        Intent intent =
				                (Intent) result.get(AccountManager.KEY_INTENT);
				        Main.this.startActivityForResult(intent, Main.GRANT_AUTH_TOKEN);
				        return;
				}
				
				authToken =
				        result.getString(AccountManager.KEY_AUTHTOKEN);
				Log.d(TAG, "run: authToken="+authToken);
				if (authToken != null) {
					client = new DefaultHttpClient();
					ProgressDialog dialog = new ProgressDialog(Main.this);
					AsyncUpdate update =
						new AsyncUpdate(
								Main.this,
								client,
								getListView(),
								dialog);
					AsyncCookie async =
						new AsyncCookie(
								Main.this,
								dialog,
								client,
								new InvalidateTokenListener(),
								update);
					async.execute(authToken);
				}
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

        if (account != null) {
        	asyncAuthToken();
        }
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
		startActivityForResult(intent, ACCOUNT_SELECTED);
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
    
    void asyncAuthToken() {
		client = new DefaultHttpClient();
		ProgressDialog dialog = new ProgressDialog(this);
		AsyncUpdate update =
			new AsyncUpdate(
					this,
					client,
					getListView(),
					dialog);
		AsyncCookie cookie =
			new AsyncCookie(
					this,
					dialog,
					client,
					new InvalidateTokenListener(),
					update);
		AsyncAuthToken async =
			new AsyncAuthToken(
					this,
					handler,
					account,
					new NutrimancerCallback(),
					cookie,
					dialog
					);
		async.execute();
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
		case R.id.menu_add_food_log:
			Intent intent = new Intent();
			intent.setClass(this, AddFoodLog.class);
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
			startActivity(intent);
			break;
		case R.id.menu_select_account:
			startSelectAccount();
			break;
		}
		return true;
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "onActivityResult: requestCode="+requestCode+", resultCode="+resultCode+", data="+data);
            switch (requestCode) {
            case ACCOUNT_SELECTED:
            	if (resultCode == RESULT_OK && data != null) {
            		account =
            			(Account)data.getExtras().get(SelectAccount.DEFAULT_ACCOUNT_KEY);
            		Log.d(TAG, "onActivityResult: account="+account);
            		asyncAuthToken();
            	}
            case GRANT_AUTH_TOKEN:
                    if (resultCode == RESULT_OK && authToken == null) {
                    	asyncAuthToken();
                    	Log.d(TAG, "onActivityResult: GRAND_AUTH_TOKEN: authToken="+authToken);
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
    }
}
