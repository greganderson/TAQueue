package com.familybiz.greg.taqueue.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.Instructor;
import com.familybiz.greg.taqueue.model.School;

/**
 * A fragment with a listview inside.
 *
 * Created by Greg Anderson
 */
public abstract class ListFragment extends Fragment {

	protected static ArrayAdapter<String> mArrayAdapter;

	protected static School[] mSchools;
	protected static School mSelectedSchool;
	protected static Instructor mSelectedInstructor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout rootLayout = new LinearLayout(getActivity());
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		if (mArrayAdapter == null)
			mArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item);

		final ListView listView = new ListView(getActivity());
		listView.setAdapter(mArrayAdapter);
		rootLayout.addView(listView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));


		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Object o = getSelectedItem(adapterView.getItemAtPosition(i).toString());
				itemSelectedListener(o);
			}
		});

		return rootLayout;
	}

	abstract Object getSelectedItem(String name);

	abstract void itemSelectedListener(Object o);
}
