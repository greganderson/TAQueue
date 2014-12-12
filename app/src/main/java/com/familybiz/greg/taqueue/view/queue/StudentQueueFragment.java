package com.familybiz.greg.taqueue.view.queue;

import android.view.View;
import android.widget.AdapterView;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.model.User;

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
		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executeDeleteRequest("/students/" + user.getId(), user.getId(), user.getToken());
		mReadyToRefresh = false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		// Do nothing
	}
}
