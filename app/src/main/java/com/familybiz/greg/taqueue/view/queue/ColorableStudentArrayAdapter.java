package com.familybiz.greg.taqueue.view.queue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.R;

/**
 * Created by Greg Anderson
 */
public class ColorableStudentArrayAdapter extends ArrayAdapter<String> {

	private int[] mColors;

	public ColorableStudentArrayAdapter(Context context, int resource) {
		super(context, resource);
		mColors = getContext().getResources().getIntArray(R.array.ta_colors);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView)super.getView(position, convertView, parent);

		// Check if student is being helped

		String nameLocation = super.getItem(position);
		String[] items = MainActivity.getNameAndLocation(nameLocation);
		String name = items[0];
		String location = items[1];

		int taLocation = QueueFragment.indexOfHelpingTA(name, location);
		if (taLocation != -1)
			textView.setBackgroundColor(mColors[taLocation]);
		else
			textView.setBackgroundColor(getContext().getResources().getColor(R.color.light_background_color));

		return textView;
	}
}