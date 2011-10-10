package com.example.package2json;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView message = (TextView)findViewById(R.id.message_text);
        
        AsyncPackages async = new AsyncPackages(this, message);
        async.execute();
   }
}