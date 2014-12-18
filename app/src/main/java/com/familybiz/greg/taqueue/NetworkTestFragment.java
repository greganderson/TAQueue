package com.familybiz.greg.taqueue;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Greg Anderson
 */
public class NetworkTestFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		RelativeLayout rootLayout = new RelativeLayout(getActivity());

		Button retry = new Button(getActivity());
		retry.setText(getString(R.string.retry_connection_button));
		retry.setBackgroundResource(R.drawable.custom_button);

		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		retry.setLayoutParams(buttonParams);

		rootLayout.addView(retry);

		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ConnectivityManager manager = (ConnectivityManager)getActivity()
						.getSystemService(Context.CONNECTIVITY_SERVICE);

				NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
				if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
					if (mOnNetworkReconnectedListener != null)
						mOnNetworkReconnectedListener.onNetworkReconnected();
				}
				else
					Toast.makeText(getActivity(), getString(R.string.network_timeout_toast), Toast.LENGTH_SHORT).show();
			}
		});

		return rootLayout;
	}


	/***************************** LISTENERS *****************************/


	public interface OnNetworkReconnectedListener {
		public void onNetworkReconnected();
	}

	private OnNetworkReconnectedListener mOnNetworkReconnectedListener;

	public void setOnNetworkReconnectedListener(OnNetworkReconnectedListener onNetworkReconnectedListener) {
		mOnNetworkReconnectedListener = onNetworkReconnectedListener;
	}
}
