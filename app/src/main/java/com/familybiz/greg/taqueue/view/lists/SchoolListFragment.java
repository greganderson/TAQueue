package com.familybiz.greg.taqueue.view.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.network.SchoolRequest;

/**
 * Represents the list of schools.
 *
 * Created by Greg Anderson
 */
public class SchoolListFragment extends ListFragment {

	// Makes a network call to populate the data
	private SchoolRequest mSchoolRequest;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mSchoolRequest = new SchoolRequest();

		mSchoolRequest.setOnSchoolsReceivedListener(new SchoolRequest.OnSchoolsReceivedListener() {
			@Override
			public void onSchoolsReceived(School[] schools) {
				mSchools = schools;

				// Update the list of schools
				mArrayAdapter.clear();
				String[] names = new String[schools.length];
				for (int i = 0; i < names.length; i++)
					names[i] = schools[i].toString();

				mArrayAdapter.addAll(names);
			}
		});

		// Populate the list and array of schools
		mSchoolRequest.populateSchoolData();

		return super.onCreateView(inflater, container, savedInstanceState);
	}


	/**
	 * Finds the school with the given name.  Returns null if it doesn't exist.
	 */
	@Override
	Object getSelectedItem(String name) {
		for (School school : mSchools)
			if (school.getName().equals(name))
				return school;
		return null;
	}

	@Override
	void itemSelectedListener(Object school) {
		if (mOnSchoolSelectedListener != null)
			mOnSchoolSelectedListener.onSchoolSelected((School)school);
	}


	/***************************** LISTENERS *****************************/


	// School selected

	public interface OnSchoolSelectedListener {
		public void onSchoolSelected(School school);
	}

	private OnSchoolSelectedListener mOnSchoolSelectedListener;

	public void setOnSchoolSelectedListener(OnSchoolSelectedListener onSchoolSelectedListener) {
		mOnSchoolSelectedListener = onSchoolSelectedListener;
	}
}
