package com.familybiz.greg.taqueue.view.queue;

import android.view.View;
import android.widget.AdapterView;

import com.familybiz.greg.taqueue.MainActivity;

/**
 * Created by Greg Anderson
 */
public class StudentQueueFragment extends QueueFragment {

	public void enterQueue() {
		MainActivity.NETWORK_REQUEST.executeGetRequest("/queue/enter_queue", MainActivity.getUser().getId(), MainActivity.getUser().getToken());
	}

	public void exitQueue() {
		MainActivity.NETWORK_REQUEST.executeGetRequest("/queue/exit_queue", MainActivity.getUser().getId(), MainActivity.getUser().getToken());
	}

	public void signOut() {
		mQueueRequest.deleteUser("students");
		mReadyToRefresh = false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		// Do nothing
	}

	@Override
	void updateTabs() {
		/*
		// Queue active

		ActionBar.Tab tab = MainActivity.mActionBar.getTabAt(0);
		if (mQueue.isActive()) {
			if (!tab.getText().equals(getString(R.string.activate_tab_label)))
				tab.select();
		}
		else {
			if (tab.getText().equals(getString(R.string.activate_tab_label)))
				tab.select();
		}
		*/
	}
}
