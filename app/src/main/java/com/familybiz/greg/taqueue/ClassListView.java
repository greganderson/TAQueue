package com.familybiz.greg.taqueue;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.UUID;

/**
 * Created by Greg Anderson
 */
public class ClassListView extends Fragment implements ListAdapter {

	private UUID[] mClasses;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ListView classList = new ListView(getActivity());
		classList.setAdapter(this);

		classList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i("CLICK", "I got clicked");
			}
		});

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public int getCount() {
		// TODO: Implement
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public Object getItem(int i) {
		return i;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public int getItemViewType(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		// TODO: Implement
		return null;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int i) {
		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver dataSetObserver) { }

	@Override
	public void unregisterDataSetObserver(DataSetObserver dataSetObserver) { }
}
