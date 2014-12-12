package com.familybiz.greg.taqueue.view.queue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Greg Anderson
 */
public class ColorableStudentArrayAdapter extends ArrayAdapter<String> {

	public ColorableStudentArrayAdapter(Context context, int resource) {
		super(context, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView)super.getView(position, convertView, parent);

		// Check if student is being helped
		/*
		String nameLocation = super.getItem(position);
		String[] items = MainActivity.getNameAndLocation(nameLocation);
		String name = items[0];
		String location = items[1];
		if (QueueFragment.STUDENTS_BEING_HELPED.containsKey(name))
			if (QueueFragment.STUDENTS_BEING_HELPED.get(name).contains(location))
				textView.setBackgroundColor(getContext().getResources().getColor(R.color.ta_highlight_color));
		*/

		return textView;
	}
}
