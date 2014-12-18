package com.familybiz.greg.taqueue.view.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.School;
import com.familybiz.greg.taqueue.network.SchoolRequest;

/**
 * Represents the list of schools.
 *
 * Created by Greg Anderson
 */
public class SchoolListFragment extends ListFragment implements SchoolRequest.OnSchoolsReceivedListener {

	// Makes a network call to populate the data
	private SchoolRequest mSchoolRequest;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mSchoolRequest = new SchoolRequest();

		mSchoolRequest.setOnSchoolsReceivedListener(this);

		// Populate the list and array of schools
		mSchoolRequest.populateSchoolData();

		View rootLayout = super.onCreateView(inflater, container, savedInstanceState);

		mLabelView.setText(getString(R.string.school_list_label));

		return rootLayout;
	}

	@Override
	public void onStop() {
		mSchoolRequest.clearNetworkListener();
		super.onStop();
	}

	public void refreshData() {
		mSchoolRequest.populateSchoolData();
	}

	@Override
	public void onSchoolsReceived(School[] schools) {
		mSchools = schools;

		// Update the list of schools
		mArrayAdapter.clear();
		String[] names = new String[schools.length];
		for (int i = 0; i < names.length; i++)
			names[i] = schools[i].toString();

		mArrayAdapter.addAll(names);

		if (mOnSchoolsLoadedListener != null)
			mOnSchoolsLoadedListener.onSchoolsLoaded();
	}

	/**
	 * Finds the school with the given name.  Returns null if it doesn't exist.
	 */
	@Override
	public Object getSelectedItem(String name) {
		for (School school : mSchools)
			if (school.getName().equals(name))
				return school;
		return null;
	}

	@Override
	public void itemSelectedListener(Object school) {
		if (mOnSchoolSelectedListener != null)
			mOnSchoolSelectedListener.onSchoolSelected((School)school);
	}


	/***************************** LISTENERS *****************************/


	// Done loading data

	public interface OnSchoolsLoadedListener {
		public void onSchoolsLoaded();
	}

	private OnSchoolsLoadedListener mOnSchoolsLoadedListener;

	public void setOnSchoolsLoadedListener(OnSchoolsLoadedListener onSchoolsLoadedListener) {
		mOnSchoolsLoadedListener = onSchoolsLoadedListener;
	}


	// School selected

	public interface OnSchoolSelectedListener {
		public void onSchoolSelected(School school);
	}

	private OnSchoolSelectedListener mOnSchoolSelectedListener;

	public void setOnSchoolSelectedListener(OnSchoolSelectedListener onSchoolSelectedListener) {
		mOnSchoolSelectedListener = onSchoolSelectedListener;
	}
}
