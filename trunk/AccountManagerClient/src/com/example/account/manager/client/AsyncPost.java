package com.example.account.manager.client;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class AsyncPost extends AsyncTask<Void, Void, Void> {

	ProgressDialog dialog = null;
	Context context = null;
	Handler handler = null;
	String logDate = null;
	String time = null;
	String food = null;
	String kcal = null;
	String salt = null;

	public AsyncPost(
			Context context,
			Handler handler,
			String logDate,
			String time,
			String food,
			String kcal,
			String salt) {
		this.context = context;
		this.handler = handler;
		this.logDate = logDate;
		this.time = time;
		this.food = food;
		this.kcal = kcal;
		this.salt = salt;
	}
	
	@Override
	protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle(R.string.post_title);
        try { dialog.show(); } catch (Exception e) {}
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
        try { dialog.dismiss(); } catch (Exception e) {}
	}
}
