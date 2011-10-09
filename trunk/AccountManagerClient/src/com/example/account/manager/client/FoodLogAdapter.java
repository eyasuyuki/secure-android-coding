package com.example.account.manager.client;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.account.manager.client.provider.FoodLog;

public class FoodLogAdapter extends ArrayAdapter<FoodLog> {

	LayoutInflater inflater;
	List<FoodLog> foodLogList = null;
	
	public FoodLogAdapter(
			Context context,
			int textViewResourceId,
			List<FoodLog> foodLogList) {
		super(context, textViewResourceId, foodLogList);
		this.foodLogList = foodLogList;
		inflater =
			(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.food_log_row, null);
		}
		FoodLog item = this.getItem(position);
		TextView logDate = (TextView)convertView.findViewById(R.id.log_date_text);
		TextView time = (TextView)convertView.findViewById(R.id.time_text);
		TextView food = (TextView)convertView.findViewById(R.id.foof_text);
		TextView kcal = (TextView)convertView.findViewById(R.id.kcal_text);
		TextView salt = (TextView)convertView.findViewById(R.id.salt_text);
		logDate.setText(item.getLogDate());
		time.setText(item.getTime());
		food.setText(item.getFood());
		kcal.setText(Double.toString(item.getKcal()));
		salt.setText(Double.toString(item.getSalt()));
		return convertView;
	}
}
