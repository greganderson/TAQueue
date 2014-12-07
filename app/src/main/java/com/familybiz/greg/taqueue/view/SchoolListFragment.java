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
import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.network.SchoolRequest;

/**
 * Created by Greg Anderson
 */
public class SchoolListFragment extends Fragment {

	private SchoolRequest mSchoolRequest;
	private ArrayAdapter<String> mArrayAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mSchoolRequest = new SchoolRequest();

		LinearLayout rootLayout = new LinearLayout(getActivity());
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		mArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.school_list);

		ListView listView = new ListView(getActivity());
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				String item = mArrayAdapter.getItem(i);
				mArrayAdapter.add(item);
			}
		});
		listView.setAdapter(mArrayAdapter);
		rootLayout.addView(listView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		mSchoolRequest.setOnSchoolsReceivedListener(new SchoolRequest.OnSchoolsReceivedListener() {
			@Override
			public void onSchoolsReceived(School[] schools) {
				mArrayAdapter.clear();
				String[] names = new String[schools.length];
				for (int i = 0; i < names.length; i++)
					names[i] = schools[i].toString();
				mArrayAdapter.addAll(names);
			}
		});

		// Populate the list
		mSchoolRequest.populateSchoolData();

		return rootLayout;
	}
}
