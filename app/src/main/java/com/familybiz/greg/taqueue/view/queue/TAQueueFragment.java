package com.familybiz.greg.taqueue.view.queue;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.familybiz.greg.taqueue.MainActivity;
import com.familybiz.greg.taqueue.R;
import com.familybiz.greg.taqueue.model.User;
import com.familybiz.greg.taqueue.model.queue.QueueStudent;
import com.familybiz.greg.taqueue.model.queue.QueueTA;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Greg Anderson
 */
public class TAQueueFragment extends QueueFragment {

	/**
	 * Returns the QueueTA object of the user.
	 */
	private QueueTA getSelfData() {
		String id = MainActivity.getUser().getId();
		for (QueueTA ta : mQueue.getTAs())
			if (ta.getId().equals(id))
				return ta;
		return null;
	}

	public void activateQueue()  {
		setActiveQueue(true);
	}

	public void deactivateQueue() {
		setActiveQueue(false);
	}

	/**
	 * Helper method for activating and deactivating the queue.
	 */
	private void setActiveQueue(boolean active) {
		JSONObject options = new JSONObject();
		try {
			options.put("active", active);
			changeQueueAttributes(options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void freezeQueue() {
		setFrozenQueue(true);
	}

	public void unfreezeQueue() {
		setFrozenQueue(false);
	}

	/**
	 * Helper method for freezing and unfreezing the queue.
	 */
	private void setFrozenQueue(boolean frozen) {
		JSONObject options = new JSONObject();
		try {
			options.put("frozen", frozen);
			changeQueueAttributes(options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void acceptStudent(String name, String location) {
		actionOnStudent(name, location, "ta_accept");
	}

	public void removeStudent(String name, String location) {
		actionOnStudent(name, location, "ta_remove");
	}

	public void putBackStudent(String name, String location) {
		actionOnStudent(name, location, "ta_putback");
	}

	/**
	 * Helper method that accepts, removes, or puts back a student.
	 */
	private void actionOnStudent(String name, String location, String action) {
		QueueStudent student = getStudent(name, location);
		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executeGetRequest("/students/" + student.getId() + "/" + action, user.getId(), user.getToken());
	}


	/**
	 * Changes the queue status to the provided string.
	 */
	public void changeStatus(String status) {
		JSONObject options = new JSONObject();
		try {
			options.put("status", status);
			changeQueueAttributes(options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the queue to being question based (or not).
	 */
	public void changeIsQuestionBased(boolean isQuestionBased) {
		// TODO: Check why server won't let the queue be question based.
		JSONObject options = new JSONObject();
		try {
			options.put("is_question_based", isQuestionBased);
			changeQueueAttributes(options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method for all methods that change the attributes of the queue.  Makes the network
	 * calls with the correct parameters.
	 */
	private void changeQueueAttributes(JSONObject options) {
		JSONObject params = new JSONObject();
		try {
			params.put("queue", options);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		User user = MainActivity.getUser();
		MainActivity.NETWORK_REQUEST.executePutRequest("/queue", params, user.getId(), user.getToken());
	}

	@Override
	public void signOut() {
		mQueueRequest.deleteUser("tas");
		mReadyToRefresh = false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		String[] items = MainActivity.getNameAndLocation(((TextView) view).getText().toString());
		final String name = items[0];
		final String location = items[1];


		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.ta_selecting_student_popup_label));

		// Student hasn't been helped
		if (QueueFragment.indexOfHelpingTA(name, location) == -1) {
			builder.setItems(mNotYetHelpedActionOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					String clicked = mNotYetHelpedActionOptions[i].toString();
					if (clicked.equals(getString(R.string.accept_student_action))) {

						// If already helping a student, remove that student and accept the new one.
						QueueTA ta = getSelfData();
						QueueStudent student = ta.getStudent();
						if (student != null)
							removeStudent(student.getUsername(), student.getLocation());

						acceptStudent(name, location);
					}
					else
						removeStudent(name, location);
				}
			});
		}

		// Student being helped already
		else {
			builder.setItems(mAlreadyHelpedActionOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					String clicked = mAlreadyHelpedActionOptions[i].toString();
					if (clicked.equals(getString(R.string.remove_student_action)))
						removeStudent(name, location);
					else
						putBackStudent(name, location);
				}
			});
		}

		builder.show();
	}

	@Override
	void updateTabs() {

		// Queue active

		ActionBar.Tab tab = MainActivity.mActionBar.getTabAt(0);
		if (mQueue.isActive()) {
			if (tab.getText().equals(getString(R.string.activate_tab_label)))
				tab.setText(getString(R.string.deactivate_tab_label));
		}
		else {
			if (!tab.getText().equals(getString(R.string.activate_tab_label)))
				tab.setText(getString(R.string.activate_tab_label));
		}

		// Queue frozen

		tab = MainActivity.mActionBar.getTabAt(1);
		if (mQueue.isFrozen()) {
			if (!tab.getText().equals(getString(R.string.queue_frozen_label)))
				tab.setText(getString(R.string.unfreeze_tab_label));
		}
		else {
			if (tab.getText().equals(getString(R.string.queue_frozen_label)))
				tab.setText(getString(R.string.freeze_tab_label));
		}
	}
}
