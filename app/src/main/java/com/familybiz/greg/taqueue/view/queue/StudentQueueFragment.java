package com.familybiz.greg.taqueue.view.queue;

import android.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.Student;

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

		// Check if user is in queue

		Student student = (Student)MainActivity.getUser();
		ActionBar.Tab tab = MainActivity.mActionBar.getTabAt(0);

		if (mQueue.containsStudent(student.getUsername(), student.getLocation())) {
			if (!tab.getText().equals(getString(R.string.exit_queue_button_text)))
				tab.setText(getString(R.string.exit_queue_button_text));
		}
		else {
			if (!tab.getText().equals(getString(R.string.enter_queue_button_text)))
				tab.setText(getString(R.string.enter_queue_button_text));
		}
	}
}
