package com.familybiz.greg.taqueue.view;

import android.app.Fragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Greg Anderson
 */
public class LoginFragment extends Fragment {

	protected EditText mNameTextBox;
	protected EditText mPasswordTextBox;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout rootLayout = new LinearLayout(getActivity());
		rootLayout.setOrientation(LinearLayout.VERTICAL);

		mNameTextBox = new EditText(getActivity());
		mPasswordTextBox = new EditText(getActivity());

		// Set the maximum number of characters
		int maxLength = 25;
		mNameTextBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
		mPasswordTextBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

		rootLayout.addView(mNameTextBox, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		rootLayout.addView(mPasswordTextBox, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		return rootLayout;
	}
}
