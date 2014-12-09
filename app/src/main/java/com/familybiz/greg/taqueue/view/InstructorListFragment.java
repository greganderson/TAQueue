package com.familybiz.greg.taqueue.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
		Instructor[] instructors = mSelectedSchool.getInstructors();
		String[] names = new String[instructors.length];
		for (int i = 0; i < names.length; i++)
			names[i] = instructors[i].getName();

		mArrayAdapter.addAll(names);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * Find the instructor with the given name.  Returns null if it doesn't exist.
	 */
	@Override
	Object getSelectedItem(String name) {
		for (Instructor instructor : mSelectedSchool.getInstructors()) {
			if (instructor.getName().equals(name)) {
				mSelectedInstructor = instructor;
				return instructor;
			}
		}
		return null;
	}

	@Override
	void itemSelectedListener(Object instructor) {
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
