package com.familybiz.greg.taqueue.view.lists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.Instructor;

/**
 * Represents the list of instructors for a school.
 *
 * Created by Greg Anderson
 */
public class InstructorListFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Populate the list of instructors.
		mArrayAdapter.clear();
		Instructor[] instructors = MainActivity.getSelectedSchool().getInstructors();
		String[] names = new String[instructors.length];
		for (int i = 0; i < names.length; i++)
			names[i] = instructors[i].getName();

		mArrayAdapter.addAll(names);

		View rootLayout = super.onCreateView(inflater, container, savedInstanceState);

		mLabelView.setText(getString(R.string.instructor_list_label));

		return rootLayout;
	}

	/**
	 * Find the instructor with the given name.  Returns null if it doesn't exist.
	 */
	@Override
	public Object getSelectedItem(String name) {
		for (Instructor instructor : MainActivity.getSelectedSchool().getInstructors())
			if (instructor.getName().equals(name))
				return instructor;
		return null;
	}

	@Override
	public void itemSelectedListener(Object instructor) {
		if (mOnInstructorSelectedListener != null)
			mOnInstructorSelectedListener.onInstructorSelected((Instructor)instructor);
	}


	/***************************** LISTENERS *****************************/


	// Instructor selected

	public interface OnInstructorSelectedListener {
		public void onInstructorSelected(Instructor instructor);
	}

	private OnInstructorSelectedListener mOnInstructorSelectedListener;

	public void setOnInstructorSelectedListener(OnInstructorSelectedListener onInstructorSelectedListener) {
		mOnInstructorSelectedListener = onInstructorSelectedListener;
	}
}
