package com.familybiz.greg.taqueue.view.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.R;

/**
 * Represents the TA login screen.
 *
 * Created by Greg Anderson
 */
public class TALoginFragment extends LoginFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootLayout = super.onCreateView(inflater, container, savedInstanceState);
		mNameTextBox.setHint(getString(R.string.name_hint));
		mPasswordTextBox.setHint(getString(R.string.password_hint));
		return rootLayout;
	}

	@Override
	void makeNetworkCallToCreateUser(String name, String password) {
		// TODO: Implement
	}
}
