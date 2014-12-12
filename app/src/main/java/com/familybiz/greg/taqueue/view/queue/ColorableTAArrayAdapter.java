package com.familybiz.greg.taqueue.view.queue;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.familybiz.greg.taqueue.R;

/**
 * Created by Greg Anderson
 */
public class ColorableTAArrayAdapter extends ArrayAdapter<String> {

	private int[] mTAColors;

	public ColorableTAArrayAdapter(Context context, int resource) {
		super(context, resource);
		mTAColors = getContext().getResources().getIntArray(R.array.ta_colors);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView)super.getView(position, convertView, parent);
		textView.setBackgroundColor(mTAColors[position]);
		return textView;
	}
}
