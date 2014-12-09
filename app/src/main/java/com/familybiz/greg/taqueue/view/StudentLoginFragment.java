package com.familybiz.greg.taqueue.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.Student;
import com.familybiz.greg.taqueue.network.StudentRequest;

/**
 * Represents the student login screen.
 *
 * Created by Greg Anderson
 */
public class StudentLoginFragment extends LoginFragment implements StudentRequest.OnStudentCreatedListener {

	// Network
	private StudentRequest mStudentRequest;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mStudentRequest = new StudentRequest();
		mStudentRequest.setOnStudentCreatedListener(this);

		View rootLayout = super.onCreateView(inflater, container, savedInstanceState);
		mNameTextBox.setHint(getString(R.string.name_hint));
		mPasswordTextBox.setHint(getString(R.string.location_hint));
		return rootLayout;
	}

	@Override
	void makeNetworkCallToCreateUser() {
		// TODO: Implement
	}

	@Override
	public void onStudentCreated(Student student) {
		if (mOnStudentLoginSuccessListener != null)
			mOnStudentLoginSuccessListener.onStudentLoginSuccess();
	}


	/***************************** LISTENERS *****************************/


	public interface OnStudentLoginSuccessListener {
		public void onStudentLoginSuccess();
	}

	private OnStudentLoginSuccessListener mOnStudentLoginSuccessListener;

	public void setOnStudentLoginSuccessListener(OnStudentLoginSuccessListener onStudentLoginSuccessListener) {
		mOnStudentLoginSuccessListener = onStudentLoginSuccessListener;
	}
}
