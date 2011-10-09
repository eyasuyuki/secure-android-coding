package com.example.account.manager.client;

import java.util.Date;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.account.manager.client.provider.FoodLog;

public class AddFoodLog extends Activity {

	String token = null;
	EditText logDate = null;
	Spinner time = null;
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
        time = (Spinner)findViewById(R.id.time_spinner);
        food = (EditText)findViewById(R.id.food_edit);
        kcal = (EditText)findViewById(R.id.kcal_edit);
        salt = (EditText)findViewById(R.id.salt_edit);
        postButton = (Button)findViewById(R.id.post_button);
        postButton.setOnClickListener(new AddFoodLogListener());

        logDate.setText(FoodLog.DF.format(new Date()));
	}
	
	final class AddFoodLogListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (token != null) {
				
			}
		}
	}

}
