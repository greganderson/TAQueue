package com.familybiz.greg.taqueue.view.login;

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
	public void onStart() {
		mStudentRequest.addListener();
		mStudentRequest.setOnStudentCreatedListener(this);
		super.onStart();
	}

	@Override
	public void onStop() {
		mStudentRequest.removeListener();
		mStudentRequest.setOnStudentCreatedListener(null);
		super.onStop();
	}

	@Override
	void makeNetworkCallToCreateUser(String name, String location) {
		mStudentRequest.createStudent(name, location);
	}

	@Override
	public void onStudentCreated(Student student) {
		if (mOnStudentLoginSuccessListener != null)
			mOnStudentLoginSuccessListener.onStudentLoginSuccess(student);
	}


	/***************************** LISTENERS *****************************/


	public interface OnStudentLoginSuccessListener {
		public void onStudentLoginSuccess(Student student);
	}

	private OnStudentLoginSuccessListener mOnStudentLoginSuccessListener;

	public void setOnStudentLoginSuccessListener(OnStudentLoginSuccessListener onStudentLoginSuccessListener) {
		mOnStudentLoginSuccessListener = onStudentLoginSuccessListener;
	}
}
