package com.familybiz.greg.taqueue.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Greg Anderson
 */
public class StudentQueueFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout rootLayout = new LinearLayout(getActivity());

		ListView listView = new ListView(getActivity());
		rootLayout.addView(listView, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		return rootLayout;
	}
}
