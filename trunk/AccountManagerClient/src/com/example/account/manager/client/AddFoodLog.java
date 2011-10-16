package com.example.account.manager.client;

import java.util.Date;

import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.account.manager.client.provider.FoodLog;

public class AddFoodLog extends Activity {
	private static final String TAG = AddFoodLog.class.getName();
	
	Handler handler = new Handler();
	
	String token = null;
	EditText logDate = null;
	Spinner spinner = null;
	String time = null;
	EditText food = null;
	EditText kcal = null;
	EditText salt = null;
	Button postButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food_log);
        
        token = getIntent().getStringExtra(
        		AccountManager.KEY_AUTHTOKEN);
    
        logDate = (EditText)findViewById(R.id.log_date_edit);
        spinner = (Spinner)findViewById(R.id.time_spinner);
        food = (EditText)findViewById(R.id.food_edit);
        kcal = (EditText)findViewById(R.id.kcal_edit);
        salt = (EditText)findViewById(R.id.salt_edit);
        postButton = (Button)findViewById(R.id.post_button);
        postButton.setOnClickListener(new AddFoodLogListener());
        
        spinner.setOnItemSelectedListener(
        		new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapter, View view,
							int pos, long id) {
						time = (String)adapter.getItemAtPosition(pos);
						Log.d(TAG, "onItemSelected: time="+time);
					}

					@Override
					public void onNothingSelected(AdapterView<?> adapter) {
						// TODO Auto-generated method stub
					}});

        logDate.setText(FoodLog.DF.format(new Date()));
	}
	
	final class AddFoodLogListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (token != null) {
				DefaultHttpClient client = new DefaultHttpClient();
				ProgressDialog dialog = new ProgressDialog(AddFoodLog.this);
				AsyncPost post =
					new AsyncPost(
							AddFoodLog.this,
							client,
							logDate.getText().toString(),
							time,
							food.getText().toString(),
							kcal.getText().toString(),
							salt.getText().toString(),
							dialog
					);
				AsyncCookie cookie =
					new AsyncCookie(
							AddFoodLog.this,
							dialog,
							client,
							new InvalidateTokenListener(),
							post);
				cookie.execute(token);
			}
		}
	}

	final class InvalidateTokenListener implements TokenListener {
		@Override
		public void invalidate() {
			AccountManager am = AccountManager.get(AddFoodLog.this);
			am.invalidateAuthToken(AsyncAuthToken.APP_ENGINE, token); // dispose token
			token = null;
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							AddFoodLog.this,
							AddFoodLog.this.getString(R.string.invalidate_token_message),
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
