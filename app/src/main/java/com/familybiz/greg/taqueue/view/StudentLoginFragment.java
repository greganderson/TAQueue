package com.familybiz.greg.taqueue.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.R;

/**
 * Created by Greg Anderson
 */
public class StudentLoginFragment extends LoginFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootLayout = super.onCreateView(inflater, container, savedInstanceState);
		mNameTextBox.setHint(getString(R.string.name_hint));
		mPasswordTextBox.setHint(getString(R.string.location_hint));
		return rootLayout;
	}
}
