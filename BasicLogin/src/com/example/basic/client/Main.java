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
import org.apache.http.util.EntityUtils;

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
			HttpEntity entity = null;
			try {
				entity =
					login(uriEdit.getText().toString(),
						userEdit.getText().toString(),
						passwordEdit.getText().toString());
			} catch (Exception e1) {
				Toast.makeText(Main.this, e1.getLocalizedMessage(), Toast.LENGTH_LONG).show();
				e1.printStackTrace();
			}
			if (entity != null) {
				StringBuffer buf = new StringBuffer();
				InputStream in = null;
				try {
					in = entity.getContent();
					int c = -1;
					while ((c = in.read()) > -1) {
						buf.append((char)c);
					}
					Toast.makeText(Main.this, buf.toString(), Toast.LENGTH_LONG).show();
					messageText.setText(buf.toString());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
    }

    /*
     * 下記を参考にしました:
     * HttpClient Tutorial - Chapter 4. HTTP authentication
     * http://hc.apache.org/httpcomponents-client-ga/tutorial/html/authentication.html
     */
    HttpEntity login(String uriString, String user, String password) {
    	final int RETRY_MAX = 2;
    	
		URI uri = URI.create(uriString);
    	HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()); 
		DefaultHttpClient client = new DefaultHttpClient();

		// set username-password credentials
		if (user != null && user.length() > 0) {
			client.getCredentialsProvider().setCredentials(
					new AuthScope(uri.getHost(), uri.getPort()),
					new UsernamePasswordCredentials(user, password));
		}

		HttpContext context = new BasicHttpContext();
		HttpGet get = new HttpGet(uriString);

		HttpResponse response = null;

		try {
			int code = HttpStatus.SC_UNAUTHORIZED;
			for (int i=0; i<RETRY_MAX; i++) {
				response = client.execute(host, get, context);
				code = response.getStatusLine().getStatusCode();
				if (code == HttpStatus.SC_OK
						| code == HttpStatus.SC_CREATED) {
					return response.getEntity();
				}
			}
			// fail
			throw new HttpResponseException(code, "Response code is "
					+ Integer.toString(code));
		} catch (Exception e) {
			e.printStackTrace();
			if (get != null) {
				get.abort();
			}
			throw new RuntimeException(e);
		}
	}
}