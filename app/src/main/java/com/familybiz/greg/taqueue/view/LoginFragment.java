package com.familybiz.greg.taqueue.view;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.familybiz.greg.taqueue.R;

/**
 * Abstract fragment containing the information and layout of a login screen.
 *
 * Created by Greg Anderson
 */
public abstract class LoginFragment extends Fragment {

	// Represents the name box for both students and TA's
	protected EditText mNameTextBox;

	// Represents both the location box (for students) and the password box (for TA's)
	// There is probably a better name for it, but I couldn't come up with it.
	protected EditText mPasswordTextBox;

	private LinearLayout mRootLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootLayout = new LinearLayout(getActivity());
		mRootLayout.setOrientation(LinearLayout.VERTICAL);

		mNameTextBox = new EditText(getActivity());
		mPasswordTextBox = new EditText(getActivity());

		// Set the maximum number of characters allowed
		int maxLength = 25;
		mNameTextBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
		mPasswordTextBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

		// Login
		Button loginButton = new Button(getActivity());
		loginButton.setText(getString(R.string.login_button));

		mRootLayout.addView(mNameTextBox, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		mRootLayout.addView(mPasswordTextBox, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		buttonParams.gravity = Gravity.CENTER_HORIZONTAL;
		mRootLayout.addView(loginButton, buttonParams);


		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ProgressBar loadingCircle = (ProgressBar)getActivity().findViewById(R.id.loading_circle);
				loadingCircle.setVisibility(View.VISIBLE);
				// TODO: Make network call
			}
		});

		return mRootLayout;
	}
}
