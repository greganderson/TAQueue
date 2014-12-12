package com.familybiz.greg.taqueue.view.login;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.TA;
import com.familybiz.greg.taqueue.network.TARequest;

/**
 * Represents the TA login screen.
 *
 * Created by Greg Anderson
 */
public class TALoginFragment extends LoginFragment implements TARequest.OnTACreatedListener {

	private TARequest mTARequest;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mTARequest = new TARequest();
		mTARequest.setOnTACreatedListener(this);

		View rootLayout = super.onCreateView(inflater, container, savedInstanceState);
		mNameTextBox.setHint(getString(R.string.name_hint));
		mPasswordTextBox.setHint(getString(R.string.password_hint));
		mPasswordTextBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		return rootLayout;
	}

	@Override
	public void onStop() {
		mTARequest.removeListener();
		mTARequest.setOnTACreatedListener(null);
		super.onStop();
	}

	@Override
	void makeNetworkCallToCreateUser(String name, String password) {
		mTARequest.createTA(name, password);
	}

	@Override
	public void onTACreated(TA ta) {
		if (mOnTALoginSuccessListener != null)
			mOnTALoginSuccessListener.onTALoginSuccess(ta);
	}


	/***************************** LISTENERS *****************************/


	public interface OnTALoginSuccessListener {
		public void onTALoginSuccess(TA ta);
	}

	private OnTALoginSuccessListener mOnTALoginSuccessListener;

	public void setOnTALoginSuccessListener(OnTALoginSuccessListener onTALoginSuccessListener) {
		mOnTALoginSuccessListener = onTALoginSuccessListener;
	}
}
