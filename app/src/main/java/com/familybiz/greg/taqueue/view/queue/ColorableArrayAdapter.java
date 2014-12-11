package com.familybiz.greg.taqueue.view.queue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Greg Anderson
 */
public class ColorableArrayAdapter extends ArrayAdapter<String> {

	public ColorableArrayAdapter(Context context, int resource) {
		super(context, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView)super.getView(position, convertView, parent);
		return textView;
	}
}
