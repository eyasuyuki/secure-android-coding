package com.example.basic.client;

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
	private static final String TAG = Main.class.getName();
	
	EditText uriEdit = null;
	EditText userEdit = null;
	EditText passwordEdit = null;
	Button loginButton = null;
	TextView messageText = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        uriEdit = (EditText)findViewById(R.id.uri_edit);
        userEdit = (EditText)findViewById(R.id.user_edit);
        passwordEdit = (EditText)findViewById(R.id.password_edit);
    	loginButton = (Button)findViewById(R.id.login_button);
    	messageText = (TextView)findViewById(R.id.message_text);
    	
    	loginButton.setOnClickListener(new LoginListener());
    }
    
    final class LoginListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			messageText.setText(null);
			String uriString = uriEdit.getText().toString();
			String user = userEdit.getText().toString();
			String password = passwordEdit.getText().toString();
			AsyncLogin async = new AsyncLogin(Main.this, uriString, user, password, messageText);
			async.execute();
		}
    }

}